package durranitech.openmeteonews.presentation

import app.cash.turbine.test
import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.domain.usecase.OpenMeteoWeatherUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/presentation/WeatherViewModelTest.kt
 *
 * What this tests:
 *   WeatherViewModel — lives in your presentation/ folder.
 *
 * TWO mandatory setup steps for ViewModel tests:
 *
 *   1. Dispatchers.setMain(testDispatcher)
 *      WeatherViewModel uses viewModelScope which runs on Dispatchers.Main.
 *      There is no Android main thread in a JVM unit test.
 *      We replace it with StandardTestDispatcher which we control.
 *
 *   2. advanceUntilIdle()
 *      StandardTestDispatcher does NOT run coroutines automatically.
 *      You call advanceUntilIdle() to run all pending coroutines at once.
 *      This gives you full control over WHEN things execute in a test.
 *
 * If you forget either of these, your tests will be flaky or always pass
 * even when they should fail.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OpenMeteoWeatherViewModelTest {

	private val testDispatcher = StandardTestDispatcher()

	private lateinit var mockUseCase: OpenMeteoWeatherUseCase
	private lateinit var viewModel: OpenMeteoWeatherViewModel

	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)  // MUST be before creating ViewModel
		mockUseCase = mockk()
		viewModel = OpenMeteoWeatherViewModel(mockUseCase)
	}

	@After
	fun tearDown() {
		Dispatchers.resetMain()  // MUST reset so other tests are not affected
	}

	// ── Initial state ──────────────────────────────────────────────────────

	@Test
	fun `initial state has no loading no data no error`() {
		val state = viewModel.uiState.value
		assertFalse(state.isLoading)
		assertFalse(state.isRefreshing == false)
		assertNull(state.weather)
		assertNull(state.errorMessage)
	}

	// ── LoadWeather ────────────────────────────────────────────────────────

	@Test
	fun `LoadWeather stores weather data after success`() = runTest {
		every {
			mockUseCase(
				any(), any()
			)
		} returns flowOf(AppResult.Success(fakeWeather(temperature = 38.0)))

		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		assertEquals(38.0, viewModel.uiState.value.weather?.current?.temperature!!, 0.001)
	}

	@Test
	fun `LoadWeather clears loading flag after success`() = runTest {
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(fakeWeather()))

		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		assertFalse(viewModel.uiState.value.isLoading)
	}

	@Test
	fun `LoadWeather sets errorMessage on failure`() = runTest {
		every {
			mockUseCase(
				any(), any()
			)
		} returns flowOf(AppResult.Error("No internet connection. Check your network."))

		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		assertNotNull(viewModel.uiState.value.errorMessage)
	}

	@Test
	fun `LoadWeather clears previous error before new fetch`() = runTest {
		// First call: error
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Error("error"))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()
		assertNotNull(viewModel.uiState.value.errorMessage)

		// Second call: success
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(fakeWeather()))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()
		assertNull(viewModel.uiState.value.errorMessage)
	}

	@Test
	fun `LoadWeather uses Turbine to observe emission sequence`() = runTest {
		every { mockUseCase(any(), any()) } returns flowOf(
			AppResult.Loading,
			AppResult.Success(fakeWeather()),
		)

		viewModel.uiState.test {
			val initial = awaitItem()
			assertFalse("Should not start loading", initial.isLoading)

			viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
			advanceUntilIdle()

			val final = expectMostRecentItem()
			assertFalse(final.isLoading)
			assertNull(final.errorMessage)

			cancelAndIgnoreRemainingEvents()
		}
	}

	// ── Refresh ────────────────────────────────────────────────────────────

	@Test
	fun `Refresh sets isRefreshing not isLoading`() = runTest {
		// Load initial data first
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(fakeWeather()))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		// Start refresh that stays in Loading
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Loading)
		viewModel.onIntent(OpenMeteoWeatherIntent.RefreshWeather(34.0, 72.0))
		advanceUntilIdle()

		val state = viewModel.uiState.value
		assertTrue(state.isRefreshing == true)
		assertFalse(state.isLoading)
	}

	@Test
	fun `Refresh preserves old weather data while loading`() = runTest {
		val oldWeather = fakeWeather(temperature = 25.0)

		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(oldWeather))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Loading)
		viewModel.onIntent(OpenMeteoWeatherIntent.RefreshWeather(34.0, 72.0))
		advanceUntilIdle()

		assertEquals(25.0, viewModel.uiState.value.weather?.current?.temperature!!, 0.001)
	}

	@Test
	fun `Refresh success replaces old weather with new`() = runTest {
		every {
			mockUseCase(
				any(), any()
			)
		} returns flowOf(AppResult.Success(fakeWeather(temperature = 20.0)))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		every {
			mockUseCase(
				any(), any()
			)
		} returns flowOf(AppResult.Success(fakeWeather(temperature = 40.0)))
		viewModel.onIntent(OpenMeteoWeatherIntent.RefreshWeather(34.0, 72.0))
		advanceUntilIdle()

		assertEquals(40.0, viewModel.uiState.value.weather?.current?.temperature!!, 0.001)
	}

	// ── DismissError ───────────────────────────────────────────────────────

	@Test
	fun `DismissError clears the error message`() = runTest {
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Error("fail"))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		viewModel.onIntent(OpenMeteoWeatherIntent.DismissError)

		assertNull(viewModel.uiState.value.errorMessage)
	}

	@Test
	fun `DismissError keeps weather data intact`() = runTest {
		// Soft error — refresh failed but we have data
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(fakeWeather()))
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		advanceUntilIdle()

		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Error("Refresh failed"))
		viewModel.onIntent(OpenMeteoWeatherIntent.RefreshWeather(34.0, 72.0))
		advanceUntilIdle()

		viewModel.onIntent(OpenMeteoWeatherIntent.DismissError)

		assertNull(viewModel.uiState.value.errorMessage)
		assertNotNull(viewModel.uiState.value.weather)  // still there
	}

	// ── ChangeUnit ─────────────────────────────────────────────────────────

	@Test
	fun `ChangeUnit to Fahrenheit updates unit in state`() = runTest {
		viewModel.onIntent(OpenMeteoWeatherIntent.ChangeTemperatureUnit(TemperatureUnit.FAHRENHEIT))
		assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.uiState.value.unit)
	}

	@Test
	fun `ChangeUnit back to Celsius updates unit in state`() = runTest {
		viewModel.onIntent(OpenMeteoWeatherIntent.ChangeTemperatureUnit(TemperatureUnit.FAHRENHEIT))
		viewModel.onIntent(OpenMeteoWeatherIntent.ChangeTemperatureUnit(TemperatureUnit.CELSIUS))
		assertEquals(TemperatureUnit.CELSIUS, viewModel.uiState.value.unit)
	}

	@Test
	fun `ChangeUnit does not trigger any network call`() = runTest {
		viewModel.onIntent(OpenMeteoWeatherIntent.ChangeTemperatureUnit(TemperatureUnit.FAHRENHEIT))
		verify(exactly = 0) { mockUseCase(any(), any()) }
	}

	// ── Cancellation ───────────────────────────────────────────────────────

	@Test
	fun `second LoadWeather cancels the first in-flight request`() = runTest {
		every { mockUseCase(any(), any()) } returns flowOf(AppResult.Success(fakeWeather()))

		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(34.0, 72.0))
		runCurrent()
		viewModel.onIntent(OpenMeteoWeatherIntent.LoadWeather(35.0, 73.0))
		advanceUntilIdle()

		// UseCase called twice; second replaced the first
		verify(exactly = 2) { mockUseCase(any(), any()) }
		assertFalse(viewModel.uiState.value.isLoading)
	}
}
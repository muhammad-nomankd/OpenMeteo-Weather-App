package durranitech.openmeteonews.presentation

import durranitech.openmeteonews.core.AppResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

 /**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/presentation/WeatherReducerTest.kt
 *
 * What this tests:
 *   The reduce() top-level function from WeatherViewModel.kt.
 *
 * WHY it is a top-level function and not a private method:
 *   If reduce() were private inside WeatherViewModel, you would need to:
 *     - create a WeatherViewModel
 *     - inject a mock GetWeatherUseCase
 *     - set up Dispatchers.Main replacement
 *     - use Turbine for the StateFlow
 *   ...just to test a when() block.
 *
 *   As a top-level function it takes (state, result) → state.
 *   Pure function. These 14 tests run in under 10 milliseconds total.
 */

class WeatherReducerTest {

	private val emptyState    = OpenMeteoWeatherUiState()
	private val stateWithData = OpenMeteoWeatherUiState(weather = fakeWeather())

	// ── AppResult.Loading ──────────────────────────────────────────────────

	@Test
	fun `Loading on initial load sets isLoading true`() {
		val result = emptyState.update(AppResult.Loading, isRefresh = false)
		assertTrue(result.isLoading)
		assertFalse(result.isRefreshing == true)
	}

	@Test
	fun `Loading during pull-to-refresh sets isRefreshing true not isLoading`() {
		val result = stateWithData.update(AppResult.Loading, isRefresh = true)
		assertFalse(result.isLoading)
		assertTrue(result.isRefreshing == true)
	}

	@Test
	fun `Loading clears previous error message`() {
		val stateWithError = emptyState.copy(errorMessage = "old error")
		val result         = stateWithError.update(AppResult.Loading, isRefresh = false)
		assertNull(result.errorMessage)
	}

	@Test
	fun `Loading during refresh keeps existing weather data visible`() {
		val result = stateWithData.update(AppResult.Loading, isRefresh = true)
		assertNotNull(result.weather)
	}

	// ── AppResult.Success ──────────────────────────────────────────────────

	@Test
	fun `Success stores weather data`() {
		val weather = fakeWeather(temperature = 42.0)
		val result  = emptyState.update(AppResult.Success(weather), isRefresh = true)
		assertEquals(weather, result.weather)
	}

	@Test
	fun `Success clears isLoading`() {
		val loadingState = emptyState.copy(isLoading = true)
		val result       = loadingState.update(AppResult.Success(fakeWeather()), isRefresh = false)
		assertFalse(result.isLoading)
	}

	@Test
	fun `Success clears isRefreshing`() {
		val refreshingState = stateWithData.copy(isRefreshing = true)
		val result          = refreshingState.update(AppResult.Success(fakeWeather()), isRefresh = true)
		assertFalse(result.isRefreshing == true)
	}

	@Test
	fun `Success clears error message`() {
		val stateWithError = emptyState.copy(errorMessage = "Previous error")
		val result         = stateWithError.update(AppResult.Success(fakeWeather()), isRefresh = false)
		assertNull(result.errorMessage)
	}

	@Test
	fun `Success replaces old weather with new weather`() {
		val old = fakeWeather(temperature = 20.0)
		val new = fakeWeather(temperature = 40.0)
		val result = OpenMeteoWeatherUiState(weather = old)
			.update(AppResult.Success(new), isRefresh = true)
		assertEquals(40.0, result.weather?.current?.temperature!!, 0.001)
	}

	// ── AppResult.Error ────────────────────────────────────────────────────

	@Test
	fun `Error sets errorMessage`() {
		val result = emptyState.update(AppResult.Error("No internet"), isRefresh = false)
		assertEquals("No internet", result.errorMessage)
	}

	@Test
	fun `Error clears isLoading`() {
		val loadingState = emptyState.copy(isLoading = true)
		val result       = loadingState.update(AppResult.Error("Timeout"), isRefresh = false)
		assertFalse(result.isLoading)
	}

	@Test
	fun `Error clears isRefreshing`() {
		val refreshing = stateWithData.copy(isRefreshing = true)
		val result     = refreshing.update(AppResult.Error("Refresh failed"), isRefresh = true)
		assertFalse(result.isRefreshing == true)
	}

	@Test
	fun `Error during refresh preserves existing weather data`() {
		// Refresh failed — old data should stay on screen, show Snackbar
		val result = stateWithData.update(AppResult.Error("Refresh failed"), isRefresh = true)
		assertNotNull(result.weather)
		assertNotNull(result.errorMessage)
	}

	@Test
	fun `Error with no data produces terminal error state`() {
		val result = emptyState.update(AppResult.Error("No internet"), isRefresh = false)
		assertTrue(result.isTerminalError)
	}

	@Test
	fun `Error with existing data is NOT a terminal error`() {
		val result = stateWithData.update(AppResult.Error("Refresh failed"), isRefresh = true)
		assertFalse(result.isTerminalError)
	}
}
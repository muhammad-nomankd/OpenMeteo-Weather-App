package durranitech.openmeteonews.domain.usecase

import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.domain.repository.OpenMeteoRepository
import durranitech.openmeteonews.presentation.fakeWeather
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/domain/GetWeatherUseCaseTest.kt
 *
 * What this tests:
 *   GetWeatherUseCase — lives in your domain/usecase/ folder.
 *   We mock WeatherRepository (the INTERFACE in domain/repository/).
 *   Never mock a concrete class — always mock the interface.
 *
 * The UseCase has one job: validate coordinates, then call the repository.
 * These tests prove both behaviours work.
 */
class GetWeatherUseCaseTest {

	private lateinit var mockRepository: OpenMeteoRepository
	private lateinit var useCase: OpenMeteoWeatherUseCase

	@Before
	fun setUp() {
		mockRepository = mockk()
		useCase = OpenMeteoWeatherUseCase(mockRepository)
	}

	// ── Valid coordinates reach the repository ─────────────────────────────

	@Test
	fun `valid coordinates call the repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		useCase(latitude = 34.0, longitude = 72.0).toList()

		verify(exactly = 1) { mockRepository.getForecast(any(), any()) }
	}

	@Test
	fun `useCase passes the exact coordinates to the repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		useCase(latitude = 34.0151, longitude = 71.9726).toList()

		verify { mockRepository.getForecast(latitude = 34.0151, longitude = 71.9726) }
	}

	@Test
	fun `useCase returns whatever the repository returns`() = runTest {
		val expected = fakeWeather(temperature = 45.0)
		every {
			mockRepository.getForecast(
				any(), any()
			)
		} returns flowOf(AppResult.Success(expected))

		val result = useCase(34.0, 72.0).toList()
		val success = result.first() as AppResult.Success

		assertEquals(45.0, success.data.current.temperature, 0.001)
	}

	// ── Boundary coordinates (valid edge cases) ────────────────────────────

	@Test
	fun `latitude 90 is valid and calls repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		val result = useCase(latitude = 90.0, longitude = 0.0).toList()

		assertTrue(result.isNotEmpty())
	}

	@Test
	fun `latitude -90 is valid and calls repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		val result = useCase(latitude = -90.0, longitude = 0.0).toList()

		assertTrue(result.isNotEmpty())
	}

	@Test
	fun `longitude 180 is valid and calls repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		val result = useCase(latitude = 0.0, longitude = 180.0).toList()

		assertTrue(result.isNotEmpty())
	}

	@Test
	fun `longitude -180 is valid and calls repository`() = runTest {
		every { mockRepository.getForecast(any(), any()) } returns flowOf(
			AppResult.Success(
				fakeWeather()
			)
		)

		val result = useCase(latitude = 0.0, longitude = -180.0).toList()

		assertTrue(result.isNotEmpty())
	}

	// ── Invalid coordinates never reach the repository ─────────────────────

	@Test(expected = IllegalArgumentException::class)
	fun `latitude above 90 throws IllegalArgumentException`() = runTest {
		useCase(latitude = 91.0, longitude = 72.0).toList()
	}

	@Test(expected = IllegalArgumentException::class)
	fun `latitude below -90 throws IllegalArgumentException`() = runTest {
		useCase(latitude = -91.0, longitude = 72.0).toList()
	}

	@Test(expected = IllegalArgumentException::class)
	fun `longitude above 180 throws IllegalArgumentException`() = runTest {
		useCase(latitude = 34.0, longitude = 181.0).toList()
	}

	@Test(expected = IllegalArgumentException::class)
	fun `longitude below -180 throws IllegalArgumentException`() = runTest {
		useCase(latitude = 34.0, longitude = -181.0).toList()
	}

	@Test
	fun `invalid coordinates never call the repository`() {
		try {
			useCase(latitude = 999.0, longitude = 72.0)
		} catch (_: IllegalArgumentException) {
		}

		// Confirm the repository was never touched
		verify(exactly = 0) { mockRepository.getForecast(any(), any()) }
	}
}
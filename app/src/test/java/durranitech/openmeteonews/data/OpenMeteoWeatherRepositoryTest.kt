package durranitech.openmeteonews.data

import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.data.remote.WeatherApiService
import durranitech.openmeteonews.data.remote.dto.CurrenWeatherDto
import durranitech.openmeteonews.data.remote.dto.DailyWeatherDto
import durranitech.openmeteonews.data.remote.dto.HourlyWeatherDto
import durranitech.openmeteonews.data.remote.dto.WeatherResponseDto
import durranitech.openmeteonews.data.repository.OpenMeteoRepositoryImp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException
import java.time.LocalDate

/**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/data/WeatherRepositoryTest.kt
 *
 * What this tests:
 *   WeatherRepositoryImpl — the class inside your data/ folder.
 *   We mock WeatherApiService so no real network is used.
 *   We collect the Flow emissions and check Loading → Success / Error.
 */
class OpenMeteoWeatherRepositoryImpTest {

	private lateinit var mockApi: WeatherApiService
	private lateinit var repository: OpenMeteoRepositoryImp

	@Before
	fun setUp() {
		mockApi = mockk()
		repository = OpenMeteoRepositoryImp(mockApi)
	}

	@Test
	fun `getForecast returns Loading and then Success on a valid API response`() = runTest {
		val mockApi = mockk<WeatherApiService>()
		val repository = OpenMeteoRepositoryImp(mockApi)

		coEvery {
			mockApi.getForecast(
				any(),
				longitude = any(),
				current = any(),
				hourly = any(),
				daily = any(),
				timezone = any(),
				forecastDays = any(),
				windSpeedUnit = any()
			)
		} returns fakeWeatherResponseDto()

		val emissions = repository.getForecast(34.0,36.0).toList()

		assertEquals(2,emissions.size)
		assertTrue(emissions[0] is AppResult.Loading)
		assertTrue(emissions[1] is AppResult.Success)
	}

	@Test
	fun `getForecast Success contains correct temperature from API`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto(currentTemp = 38.5)

		val emissions = repository.getForecast(34.0, 72.0).toList()
		val success = emissions[1] as AppResult.Success

		assertEquals(38.5, success.data.current.temperature, 0.001)
	}

	@Test
	fun `getForecast emits exactly 2 items no extra emissions`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto()

		val emissions = repository.getForecast(34.0, 72.0).toList()

		assertEquals(2, emissions.size)
	}

	@Test
	fun `getForecast emits Error when device has no internet`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} throws UnknownHostException("No host")

		val emissions = repository.getForecast(34.0, 72.0).toList()

		assertTrue(emissions[0] is AppResult.Loading)
		assertTrue(emissions[1] is AppResult.Error)
	}


	@Test
	fun `getForecast emits Error with user-friendly message on IOException`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} throws IOException("Timeout")

		val emissions = repository.getForecast(34.0, 72.0).toList()
		val error = emissions[1] as AppResult.Error

		assertTrue(
			"Expected a user-friendly message, got: ${error.message}",
			error.message.contains(
				"network", ignoreCase = true
			) || error.message.contains(
				"internet", ignoreCase = true
			) || error.message.contains("retry", ignoreCase = true),
		)
	}

	@Test
	fun `getForecast emits Error when generic exception thrown`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} throws RuntimeException("Something unexpected")

		val emissions = repository.getForecast(34.0, 72.0).toList()

		assertTrue(emissions[1] is AppResult.Error)
	}

	// ── Data mapping ───────────────────────────────────────────────────────

	@Test
	fun `getForecast returns 7 daily items for a 7-day response`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto(days = 7)

		val success = repository.getForecast(34.0, 72.0).toList()[1] as AppResult.Success

		assertEquals(7, success.data.daily.size)
	}

	@Test
	fun `getForecast first daily item is labeled Today`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto()

		val success = repository.getForecast(34.0, 72.0).toList()[1] as AppResult.Success

		assertEquals("Today", success.data.daily.first().dayLabel)
	}

	@Test
	fun `getForecast limits hourly data to 24 items`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto(hourlyCount = 168)

		val success = repository.getForecast(34.0, 72.0).toList()[1] as AppResult.Success

		assertEquals(24, success.data.hourly.size)
	}

	// ── Verify API interaction ─────────────────────────────────────────────

	@Test
	fun `getForecast calls the API exactly once`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto()

		repository.getForecast(34.0, 72.0).toList()

		coVerify(exactly = 1) {
			mockApi.getForecast(any(), any(), any(), any(), any(), any(), any(), any())
		}
	}

	@Test
	fun `getForecast passes the exact coordinates to the API`() = runTest {
		coEvery {
			mockApi.getForecast(
				any(), any(), any(), any(), any(), any(), any(), any()
			)
		} returns fakeWeatherResponseDto()

		repository.getForecast(latitude = 34.0151, longitude = 71.9726).toList()

		coVerify {
			mockApi.getForecast(
				latitude = 34.0151,
				longitude = 71.9726,
				any(), any(), any(), any(), any(), any(),
			)
		}
	}
}
internal fun fakeWeatherResponseDto(
	currentTemp: Double = 30.0,
	days: Int = 7,
	hourlyCount: Int = 24,
) = WeatherResponseDto(
	latitude = 34.0,
	longitude = 72.0,
	elevation = 300.0,
	timezone = "Asia/Karachi",
	timezoneAbbreviation = "PKT",
	current = CurrenWeatherDto(
		time = "2024-07-01T14:00",
		interval = 900,
		temperature = currentTemp,
		humidity = 55,
		apparentTemperature = 33.0,
		isDay = 1,
		precipitation = 0.0,
		rain = 0.0,
		showers = 0.0,
		snowfall = 0.0,
		weatherCode = 0,
		cloudCover = 10,
		surfacePressure = 1012.0,
		windSpeed = 12.0,
		windDirection = 270,
		windGusts = 18.0,
	),
	hourly = HourlyWeatherDto(
		time = List(hourlyCount) { i -> "2024-07-01T${i.toString().padStart(2, '0')}:00" },
		temperature = List(hourlyCount) { 28.0 },
		precipitationProbability = List(hourlyCount) { 10 },
		weatherCode = List(hourlyCount) { 0 },
		windSpeed = List(hourlyCount) { 15.0 },
		humidity = List(hourlyCount) { 50 },
	),
	daily = DailyWeatherDto(
		time = List(days) { i ->
			LocalDate.now().plusDays(i.toLong()).toString()
		},
		weatherCode = List(days) { 0 },
		tempMax = List(days) { 35.0 },
		tempMin = List(days) { 22.0 },
		sunrise = List(days) { "2024-07-01T05:20" },
		sunset = List(days) { "2024-07-01T19:45" },
		precipitationSum = List(days) { 0.0 },
		precipitationProbabilityMax = List(days) { 0 },
		windSpeedMax = List(days) { 22.0 },
		uvIndexMax = List(days) { 6.0 },
	),
	utcoffsetSeconds = "20",
)
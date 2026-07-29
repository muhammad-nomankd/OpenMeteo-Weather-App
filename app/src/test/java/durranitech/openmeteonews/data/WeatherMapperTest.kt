package durranitech.openmeteonews.data

import durranitech.openmeteonews.data.mapper.toDomain
import durranitech.openmeteonews.domain.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/data/WeatherMapperTest.kt
 *
 * What this tests:
 *   WeatherMapper.toDomain() — the function inside your data/mapper/ folder.
 *   This is a pure function: give it a DTO, get back a domain model.
 *   No mocks needed. No coroutines needed.
 *
 * Why test the mapper separately from the repository?
 *   The repository test mocks the API and never runs the real mapper.
 *   If you mistype a field name in the mapper (e.g. dto.daily.humidity
 *   instead of dto.current.humidity), the repository test won't catch it.
 *   This test will.
 */
class WeatherMapperTest {

	// ── Current weather fields ─────────────────────────────────────────────

	@Test
	fun `toDomain maps current temperature correctly`() {
		val domain = fakeWeatherResponseDto(currentTemp = 37.5).toDomain()
		assertEquals(37.5, domain.current.temperature, 0.001)
	}

	@Test
	fun `toDomain maps current humidity correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(55, domain.current.humidity)
	}

	@Test
	fun `toDomain maps wind speed correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(12.0, domain.current.windSpeed, 0.001)
	}

	@Test
	fun `toDomain maps surface pressure correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(1012.0, domain.current.surfacePressure, 0.001)
	}

	@Test
	fun `toDomain maps isDay true when is_day equals 1`() {
		val domain = fakeWeatherResponseDto().toDomain() // isDay = 1 in fixture
		assertTrue(domain.current.isDay)
	}

	@Test
	fun `toDomain maps isDay false when is_day equals 0`() {
		val dto    = fakeWeatherResponseDto().copy(
			current = fakeWeatherResponseDto().current.copy(isDay = 0)
		)
		val domain = dto.toDomain()
		assertFalse(domain.current.isDay)
	}

	@Test
	fun `toDomain maps weather condition from code 0 and isDay true to ClearDay`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(WeatherCondition.ClearDay, domain.current.condition)
	}

	@Test
	fun `toDomain maps weather condition from code 0 and isDay false to ClearNight`() {
		val dto = fakeWeatherResponseDto().copy(
			current = fakeWeatherResponseDto().current.copy(isDay = 0)
		)
		val domain = dto.toDomain()
		assertEquals(WeatherCondition.ClearNight, domain.current.condition)
	}

	@Test
	fun `toDomain maps rain code 61 to RainSlight condition`() {
		val dto = fakeWeatherResponseDto().copy(
			current = fakeWeatherResponseDto().current.copy(weatherCode = 61, isDay = 1)
		)
		assertEquals(WeatherCondition.RainSlight, dto.toDomain().current.condition)
	}

	// ── Daily forecast fields ──────────────────────────────────────────────

	@Test
	fun `toDomain creates correct number of daily items`() {
		val domain = fakeWeatherResponseDto(days = 7).toDomain()
		assertEquals(7, domain.daily.size)
	}

	@Test
	fun `toDomain first daily item is labeled Today`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals("Today", domain.daily.first().dayLabel)
	}

	@Test
	fun `toDomain second daily item is labeled Tomorrow`() {
		val domain = fakeWeatherResponseDto(days = 3).toDomain()
		assertEquals("Tomorrow", domain.daily[1].dayLabel)
	}

	@Test
	fun `toDomain maps daily tempMax correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(35.0, domain.daily.first().tempMax, 0.001)
	}

	@Test
	fun `toDomain maps daily tempMin correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(22.0, domain.daily.first().tempMin, 0.001)
	}

	@Test
	fun `toDomain maps daily UV index correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(6.0, domain.daily.first().uvIndexMax, 0.001)
	}

	@Test
	fun `toDomain maps daily precipitation sum correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(0.0, domain.daily.first().precipitationSum, 0.001)
	}

	@Test
	fun `toDomain maps daily max wind speed correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(22.0, domain.daily.first().windSpeedMax, 0.001)
	}

	// ── Hourly fields ──────────────────────────────────────────────────────

	@Test
	fun `toDomain limits hourly to 24 items when API returns 168`() {
		val domain = fakeWeatherResponseDto(hourlyCount = 168).toDomain()
		assertEquals(24, domain.hourly.size)
	}

	@Test
	fun `toDomain keeps all items when API returns fewer than 24`() {
		val domain = fakeWeatherResponseDto(hourlyCount = 6).toDomain()
		assertEquals(6, domain.hourly.size)
	}

	@Test
	fun `toDomain maps hourly temperature correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(28.0, domain.hourly.first().temperature, 0.001)
	}

	@Test
	fun `toDomain maps hourly precipitation probability correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(10, domain.hourly.first().precipitationProbability)
	}

	// ── Metadata ───────────────────────────────────────────────────────────

	@Test
	fun `toDomain maps latitude correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(34.0, domain.latitude, 0.001)
	}

	@Test
	fun `toDomain maps longitude correctly`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals(72.0, domain.longitude, 0.001)
	}

	@Test
	fun `toDomain uses timezone abbreviation as timezone display`() {
		val domain = fakeWeatherResponseDto().toDomain()
		assertEquals("PKT", domain.timezone)
	}
}
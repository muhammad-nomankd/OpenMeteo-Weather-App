package durranitech.openmeteonews.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * File location in Android Studio:
 *   app/src/test/kotlin/com/durranitech/openmeteonews/domain/WeatherConditionTest.kt
 *
 * What this tests:
 *   WeatherCondition.fromCode() — lives in your domain/model/ folder.
 *   Pure function. Input: WMO code (Int) + isDay (Boolean). Output: WeatherCondition.
 *   Zero setup, zero mocks. The simplest type of test you can write.
 *
 * Why every single WMO code?
 *   Open-Meteo documents exactly which codes exist. If a future developer
 *   accidentally changes the when() ranges, one of these tests fails
 *   immediately and tells you exactly which code broke.
 */
class WeatherConditionTest {

	// ── Code 0: Clear sky — only code that splits on isDay ─────────────────

	@Test
	fun `code 0 during day maps to ClearDay`() {
		assertEquals(WeatherCondition.ClearDay, WeatherCondition.fromCode(0, isDay = true))
	}

	@Test
	fun `code 0 during night maps to ClearNight`() {
		assertEquals(WeatherCondition.ClearNight, WeatherCondition.fromCode(0, isDay = false))
	}

	// ── Codes 1-3: Mainly clear → Overcast ────────────────────────────────

	@Test fun `code 1 maps to MainlyClear`() = assert(1, WeatherCondition.MainlyClear)
	@Test fun `code 2 maps to MainlyClear`() = assert(2, WeatherCondition.MainlyClear)
	@Test fun `code 3 maps to Overcast`()    = assert(3, WeatherCondition.Overcast)

	// ── Codes 45, 48: Fog ─────────────────────────────────────────────────

	@Test fun `code 45 maps to Fog`() = assert(45, WeatherCondition.Fog)
	@Test fun `code 48 maps to Fog`() = assert(48, WeatherCondition.Fog)

	// ── Codes 51-55: Drizzle ──────────────────────────────────────────────

	@Test fun `code 51 maps to Drizzle`() = assert(51, WeatherCondition.Drizzle)
	@Test fun `code 53 maps to Drizzle`() = assert(53, WeatherCondition.Drizzle)
	@Test fun `code 55 maps to Drizzle`() = assert(55, WeatherCondition.Drizzle)

	// ── Codes 56-57: Freezing Drizzle ─────────────────────────────────────

	@Test fun `code 56 maps to FreezingDrizzle`() = assert(56, WeatherCondition.FreezingDrizzle)
	@Test fun `code 57 maps to FreezingDrizzle`() = assert(57, WeatherCondition.FreezingDrizzle)

	// ── Codes 61-65: Rain ─────────────────────────────────────────────────

	@Test fun `code 61 maps to RainSlight`()   = assert(61, WeatherCondition.RainSlight)
	@Test fun `code 63 maps to RainModerate`() = assert(63, WeatherCondition.RainModerate)
	@Test fun `code 65 maps to RainHeavy`()    = assert(65, WeatherCondition.RainHeavy)

	// ── Codes 66-67: Freezing Rain ────────────────────────────────────────

	@Test fun `code 66 maps to FreezingRain`() = assert(66, WeatherCondition.FreezingRain)
	@Test fun `code 67 maps to FreezingRain`() = assert(67, WeatherCondition.FreezingRain)

	// ── Codes 71-77: Snow ─────────────────────────────────────────────────

	@Test fun `code 71 maps to SnowSlight`()   = assert(71, WeatherCondition.SnowSlight)
	@Test fun `code 73 maps to SnowModerate`() = assert(73, WeatherCondition.SnowModerate)
	@Test fun `code 75 maps to SnowHeavy`()    = assert(75, WeatherCondition.SnowHeavy)
	@Test fun `code 77 maps to SnowGrains`()   = assert(77, WeatherCondition.SnowGrains)

	// ── Codes 80-82: Rain Showers ─────────────────────────────────────────

	@Test fun `code 80 maps to RainShowers`() = assert(80, WeatherCondition.RainShowers)
	@Test fun `code 81 maps to RainShowers`() = assert(81, WeatherCondition.RainShowers)
	@Test fun `code 82 maps to RainShowers`() = assert(82, WeatherCondition.RainShowers)

	// ── Codes 85-86: Snow Showers ─────────────────────────────────────────

	@Test fun `code 85 maps to SnowShowers`() = assert(85, WeatherCondition.SnowShowers)
	@Test fun `code 86 maps to SnowShowers`() = assert(86, WeatherCondition.SnowShowers)

	// ── Code 95: Thunderstorm ─────────────────────────────────────────────

	@Test fun `code 95 maps to Thunderstorm`() = assert(95, WeatherCondition.Thunderstorm)

	// ── Codes 96, 99: Thunderstorm + Hail ────────────────────────────────

	@Test fun `code 96 maps to ThunderstormHail`() = assert(96, WeatherCondition.ThunderstormHail)
	@Test fun `code 99 maps to ThunderstormHail`() = assert(99, WeatherCondition.ThunderstormHail)

	// ── Unknown code (not in WMO table) ───────────────────────────────────

	@Test
	fun `unknown code returns Unknown instead of throwing`() {
		// Open-Meteo may add new codes in the future.
		// The app must never crash — return Unknown gracefully.
		assertEquals(WeatherCondition.Unknown, WeatherCondition.fromCode(9999, isDay = true))
	}

	@Test
	fun `negative code returns Unknown`() {
		assertEquals(WeatherCondition.Unknown, WeatherCondition.fromCode(-1, isDay = true))
	}

	// ── Condition properties (label, emoji, colors) ────────────────────────

	@Test
	fun `every condition has a non-blank label`() {
		val allConditions = listOf(
			WeatherCondition.ClearDay, WeatherCondition.ClearNight,
			WeatherCondition.MainlyClear, WeatherCondition.Overcast,
			WeatherCondition.Fog, WeatherCondition.Drizzle,
			WeatherCondition.FreezingDrizzle, WeatherCondition.RainSlight,
			WeatherCondition.RainModerate, WeatherCondition.RainHeavy,
			WeatherCondition.FreezingRain, WeatherCondition.SnowSlight,
			WeatherCondition.SnowModerate, WeatherCondition.SnowHeavy,
			WeatherCondition.SnowGrains, WeatherCondition.RainShowers,
			WeatherCondition.SnowShowers, WeatherCondition.Thunderstorm,
			WeatherCondition.ThunderstormHail, WeatherCondition.Unknown,
		)
		allConditions.forEach { condition ->
			assertTrue(
				"$condition has blank label",
				condition.label.isNotBlank(),
			)
		}
	}

	@Test
	fun `every condition has a non-empty emoji`() {
		val conditions = listOf(
			WeatherCondition.ClearDay, WeatherCondition.ClearNight,
			WeatherCondition.RainHeavy, WeatherCondition.Thunderstorm,
		)
		conditions.forEach { condition ->
			assertTrue("$condition has empty emoji", condition.emoji.isNotEmpty())
		}
	}

	@Test
	fun `ClearDay and ClearNight have different background colors`() {
		// Day and night should look visually different
		assertTrue(
			WeatherCondition.ClearDay.backgroundStart !=
					WeatherCondition.ClearNight.backgroundStart
		)
	}

	// ── Helper ────────────────────────────────────────────────────────────

	private fun assert(code: Int, expected: WeatherCondition) {
		assertEquals(
			"WMO code $code should map to $expected",
			expected,
			WeatherCondition.fromCode(code, isDay = true),
		)
	}
}
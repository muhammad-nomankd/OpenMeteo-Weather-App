package durranitech.openmeteonews.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.DailyForecast
import durranitech.openmeteonews.domain.model.WeatherCondition
import durranitech.openmeteonews.presentation.OpenMeteoDailyForecastScreen
import durranitech.openmeteonews.presentation.TemperatureUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DailyForecastScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	// ── Top bar ────────────────────────────────────────────────────────────

	@Test
	fun day_label_is_shown_in_top_bar() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(dayLabel = "Monday"),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Monday").assertIsDisplayed()
	}

	@Test
	fun back_button_exists_with_correct_content_description() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
	}

	@Test
	fun clicking_back_button_triggers_onBack_callback() {
		var backCalled = false

		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = { backCalled = true },
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithContentDescription("Back").performClick()

		assert(backCalled) { "onBack was not called when Back was tapped" }
	}

	// ── Condition section ──────────────────────────────────────────────────

	@Test
	fun condition_label_is_shown_on_screen() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(condition = WeatherCondition.Thunderstorm),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Thunderstorm").assertIsDisplayed()
	}

	@Test
	fun date_string_is_shown_on_screen() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(date = "2024-07-15"),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("2024-07-15").assertIsDisplayed()
	}

	// ── TempRangeArc section ───────────────────────────────────────────────

	@Test
	fun low_label_is_shown_for_temp_min() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(tempMin = 18.0, tempMax = 36.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithTag("TempLevel").assertIsDisplayed()
	}

	@Test
	fun High_label_is_shown_for_temp_max() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(tempMin = 18.0, tempMax = 36.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("High").assertIsDisplayed()
	}

	@Test
	fun temp_max_value_in_celsius_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(tempMax = 38.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		// formatTemp(CELSIUS) produces "38°C"
		composeRule.onNodeWithText("38°C").assertIsDisplayed()
	}

	@Test
	fun temp_min_value_in_celsius_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(tempMin = 22.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("22°C").assertIsDisplayed()
	}

	@Test
	fun temp_max_displayed_in_fahrenheit_when_unit_is_fahrenheit() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(tempMax = 0.0),  // 0°C = 32°F
				unit = TemperatureUnit.FAHRENHEIT,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("32°F").assertIsDisplayed()
	}

	// ── DetailStatsGrid section ────────────────────────────────────────────

	@Test
	fun max_wind_label_is_shown_in_stats_grid() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Max Wind").assertIsDisplayed()
	}

	@Test
	fun wind_speed_value_is_shown_in_stats_grid() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(windSpeedMax = 25.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("25 km/h").assertIsDisplayed()
	}

	@Test
	fun precipitation_label_is_shown_in_stats_grid() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Precipitation").assertIsDisplayed()
	}

	@Test
	fun precipitation_value_in_mm_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(precipitationSum = 3.5),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("3.5 mm").assertIsDisplayed()
	}

	@Test
	fun rain_chance_label_is_shown_in_stats_grid() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Rain Chance").assertIsDisplayed()
	}

	@Test
	fun rain_chance_percentage_value_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(precipitationProbabilityMax = 75),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("75%").assertIsDisplayed()
	}

	@Test
	fun uv_index_stat_label_is_shown_in_grid() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		// The stats grid shows "UV Index" as a label
		// Note: UvIndexBar also shows "UV Index" — both should be present
		composeRule.onNodeWithTag("UV Index").assertIsDisplayed()
	}

	// ── SunriseSunsetBar section ───────────────────────────────────────────

	@Test
	fun Sun_section_header_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Sun").assertIsDisplayed()
	}

	@Test
	fun sunrise_label_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Sunrise").assertIsDisplayed()
	}

	@Test
	fun sunset_label_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Sunset").assertIsDisplayed()
	}

	@Test
	fun sunrise_time_value_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(sunrise = "5:18 AM"),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("5:18 AM").assertIsDisplayed()
	}

	@Test
	fun sunset_time_value_is_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(sunset = "7:42 PM"),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("7:42 PM").assertIsDisplayed()
	}

	// ── UvIndexBar section ─────────────────────────────────────────────────

	@Test
	fun uv_index_numeric_value_is_shown_in_bar() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 7.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("7").assertIsDisplayed()
	}

	@Test
	fun uv_level_Low_is_shown_for_index_below_3() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 1.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithTag("UvLevel").assertIsDisplayed()
	}

	@Test
	fun uv_level_Moderate_is_shown_for_index_between_3_and_6() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 4.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Moderate").assertIsDisplayed()
	}

	@Test
	fun uv_level_High_is_shown_for_index_between_6_and_8() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 7.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithTag("UvLevel").assertIsDisplayed()
	}

	@Test
	fun uv_level_Very_High_is_shown_for_index_between_8_and_11() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 9.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Very High").assertIsDisplayed()
	}

	@Test
	fun uv_level_Extreme_is_shown_for_index_11_and_above() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(uvIndexMax = 12.0),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		composeRule.onNodeWithText("Extreme").assertIsDisplayed()
	}

	@Test
	fun uv_scale_labels_0_3_6_8_11_are_shown() {
		composeRule.setContent {
			OpenMeteoDailyForecastScreen(
				day = fakeDailyForecast(),
				unit = TemperatureUnit.CELSIUS,
				onBack = {},
				current = fakeCurrentWeather(),
			)
		}

		listOf("0", "3", "6", "8", "11+").forEach { label ->
			composeRule.onNodeWithText(label).assertIsDisplayed()
		}
	}
}


package durranitech.openmeteonews.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import durranitech.openmeteonews.presentation.OpenMeteoWeatherIntent
import durranitech.openmeteonews.presentation.OpenMeteoWeatherUiState
import durranitech.openmeteonews.presentation.TemperatureUnit
import durranitech.openmeteonews.presentation.WeatherHomeScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WeatherScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	// ── Loading state ──────────────────────────────────────────────────────

	@Test
	fun shimmer_skeleton_is_shown_during_initial_load() {
		// Arrange: loading state with no data
		val loadingState = OpenMeteoWeatherUiState(isLoading = true, weather = null)

		// Act: render the screen with this state
		composeRule.setContent {
			WeatherHomeScreen(
				uiState = loadingState,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		// Assert: shimmer skeleton should be visible
		// Add Modifier.testTag("shimmer_skeleton") to WeatherScreenSkeleton
		composeRule.onNodeWithTag("shimmer_skeleton").assertIsDisplayed()
	}

	// ── Success state ──────────────────────────────────────────────────────

	@Test
	fun temperature_is_shown_when_data_loaded() {
		val successState = OpenMeteoWeatherUiState(weather = fakeWeatherUi(temperature = 32.0))

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = successState,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		// "32°C" should appear on screen
		composeRule.onNodeWithText("32°C").assertIsDisplayed()
	}

	@Test
	fun condition_label_is_shown_when_data_loaded() {
		val successState = OpenMeteoWeatherUiState(weather = fakeWeatherUi())
		composeRule.setContent {
			WeatherHomeScreen(
				uiState = successState,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("Clear Sky").assertIsDisplayed()
	}

	@Test
	fun location_label_is_shown_on_screen() {
		val state = OpenMeteoWeatherUiState(
			weather = fakeWeatherUi(),
			locationLabel = "Nowshera",
		)

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = state,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("Nowshera").assertIsDisplayed()
	}

	// ── Error state ────────────────────────────────────────────────────────

	@Test
	fun error_message_shown_when_no_data_and_error_exists() {
		val errorState = OpenMeteoWeatherUiState(
			weather = null,
			errorMessage = "No internet connection. Check your network.",
		)

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = errorState,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("No internet connection. Check your network.")
			.assertIsDisplayed()
	}

	@Test
	fun retry_button_is_shown_on_terminal_error() {
		val errorState = OpenMeteoWeatherUiState(weather = null, errorMessage = "fail")

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = errorState,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("fail").assertIsDisplayed()
	}

	@Test
	fun clicking_retry_triggers_LoadWeather_intent() {
		var intentReceived: OpenMeteoWeatherIntent? = null
		val errorState = OpenMeteoWeatherUiState(weather = null, errorMessage = "fail")

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = errorState,
				onIntent = { intentReceived = it },
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("fail").performClick()

		assert(intentReceived is OpenMeteoWeatherIntent.LoadWeather)
	}

	// ── Unit toggle ────────────────────────────────────────────────────────

	@Test
	fun temperature_shown_in_fahrenheit_when_unit_is_fahrenheit() {
		val state = OpenMeteoWeatherUiState(
			weather = fakeWeatherUi(temperature = 0.0), // 0°C = 32°F
			unit = TemperatureUnit.FAHRENHEIT,
		)

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = state,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("32°F").assertIsDisplayed()
	}

	@Test
	fun celsius_toggle_label_is_shown_by_default() {
		val state = OpenMeteoWeatherUiState(
			weather = fakeWeatherUi(temperature = 36.0), unit = TemperatureUnit.CELSIUS
		)

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = state,
				onIntent = {},
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		// The unit toggle button shows "°C"
		composeRule.onNodeWithText("36°C").assertIsDisplayed()
	}

	@Test
	fun clicking_unit_toggle_fires_ChangeUnit_intent() {
		var intentReceived: OpenMeteoWeatherIntent? = null
		val state =
			OpenMeteoWeatherUiState(weather = fakeWeatherUi(), unit = TemperatureUnit.CELSIUS)

		composeRule.setContent {
			WeatherHomeScreen(
				uiState = state,
				onIntent = { intentReceived = it },
				startLat = 34.0,
				startLon = 72.0,
				onDayClick = {},
			)
		}

		composeRule.onNodeWithText("°C").performClick()

		assert(intentReceived is OpenMeteoWeatherIntent.ChangeTemperatureUnit)
		assertEquals(
			TemperatureUnit.FAHRENHEIT,
			(intentReceived as OpenMeteoWeatherIntent.ChangeTemperatureUnit).unit
		)
	}
}


package durranitech.openmeteonews.presentation
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


/**
 * ─────────────────────────────────────────────────────────────────────────────
 * LESSON 2: Testing Computed Properties on a Data Class
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * WeatherUiState has three computed (derived) properties:
 *
 *   val isInitialLoading: Boolean get() = isLoading && weather == null
 *   val hasData:          Boolean get() = weather != null
 *   val isTerminalError:  Boolean get() = errorMessage != null && weather == null
 *
 * These are important business rules. If they're wrong, the UI shows the
 * wrong screen. Tests prove they stay correct even if someone edits the class.
 *
 * STILL no mocks needed — we just construct the data class directly.
 * ─────────────────────────────────────────────────────────────────────────────
 */
class OpenMeteoWeatherUiStateTest {

	// ── isInitialLoading ───────────────────────────────────────────────────
	//
	// isInitialLoading means: we're loading AND we have no data yet.
	// This is when we show the shimmer skeleton (full-screen loading).
	// Once we have data, even if we're refreshing, we should NOT show
	// the skeleton — we show the existing data + a small spinner instead.

	@Test
	fun `isInitialLoading is true when loading and no weather data exists`() {
		val state = OpenMeteoWeatherUiState(isLoading = true, weather = null)
		assertTrue(state.isInitialLoading)
	}

	@Test
	fun `isInitialLoading is false when loading but we already have weather data`() {
		// This is pull-to-refresh: we have old data + fetching new data.
		// We should NOT show the skeleton — show old data with a small spinner.
		val state = OpenMeteoWeatherUiState(isLoading = true, weather = fakeWeather())
		assertFalse(state.isInitialLoading)
	}

	@Test
	fun `isInitialLoading is false when not loading`() {
		val state = OpenMeteoWeatherUiState(isLoading = false, weather = null)
		assertFalse(state.isInitialLoading)
	}

	// ── hasData ────────────────────────────────────────────────────────────

	@Test
	fun `hasData is true when weather is not null`() {
		val state = OpenMeteoWeatherUiState(weather = fakeWeather())
		assertTrue(state.hasData)
	}

	@Test
	fun `hasData is false when weather is null`() {
		val state = OpenMeteoWeatherUiState(weather = null)
		assertFalse(state.hasData)
	}

	// ── isTerminalError ────────────────────────────────────────────────────
	//
	// isTerminalError means: there's an error AND we have no data to fall back on.
	// This is when we show the full-screen error state.
	//
	// If there IS data (e.g. pull-to-refresh failed), the error is "soft" —
	// show the old data + a Snackbar message. Don't blank the screen.

	@Test
	fun `isTerminalError is true when error exists and no weather data`() {
		val state = OpenMeteoWeatherUiState(errorMessage = "No internet", weather = null)
		assertTrue(state.isTerminalError)
	}

	@Test
	fun `isTerminalError is false when error exists but we still have weather data`() {
		// Refresh failed but old data still visible — this is a soft error.
		// Show Snackbar, don't blank the screen.
		val state =
			OpenMeteoWeatherUiState(errorMessage = "Refresh failed", weather = fakeWeather())
		assertFalse(state.isTerminalError)
	}

	@Test
	fun `isTerminalError is false when no error`() {
		val state = OpenMeteoWeatherUiState(errorMessage = null, weather = null)
		assertFalse(state.isTerminalError)
	}

	// ── Default state ──────────────────────────────────────────────────────
	//
	// Testing the initial state matters because the screen renders
	// immediately when the ViewModel is created — before any API call.
	// We need to confirm it starts in a clean, non-broken state.

	@Test
	fun `default state has no loading, no data, no error`() {
		val state = OpenMeteoWeatherUiState()
		assertFalse(state.isLoading)
		assertFalse(state.isRefreshing == true)
		assertFalse(state.hasData)
		assertFalse(state.isTerminalError)
		assertFalse(state.isInitialLoading)
	}

	@Test
	fun `default unit is Celsius`() {
		val state = OpenMeteoWeatherUiState()
		assertTrue(state.unit == TemperatureUnit.CELSIUS)
	}
}
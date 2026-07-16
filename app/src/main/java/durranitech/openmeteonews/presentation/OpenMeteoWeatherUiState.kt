package durranitech.openmeteonews.presentation

import durranitech.openmeteonews.domain.model.Weather

data class OpenMeteoWeatherUiState(
	val isLoading: Boolean = false,
	val errorMessage: String? = null,
	val isRefreshing: Boolean? = null,
	val weather: Weather? = null,
	val unit: TemperatureUnit = TemperatureUnit.CELSIUS,
	val locationLabel: String? = "Akora Khattak, Nowshera"
) {
	val isInitialLoading = isLoading && weather == null && errorMessage == null
	val hasData = weather != null
	val isTerminalError = errorMessage != null && weather == null
}

fun Double.formatTemp(unit: TemperatureUnit): String {
	val value = if (unit == TemperatureUnit.FAHRENHEIT) (this * 9 / 5) + 32 else this
	return "${value.toInt()}${unit.symbol}"
}

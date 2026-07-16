package durranitech.openmeteonews.presentation

sealed class OpenMeteoWeatherIntent{
	data class LoadWeather(val latitude: Double, val longitude: Double): OpenMeteoWeatherIntent()
	data class RefreshWeather(val latitude: Double, val longitude: Double): OpenMeteoWeatherIntent()
	data class ChangeTemperatureUnit(val unit: TemperatureUnit): OpenMeteoWeatherIntent()
	data object DismissError: OpenMeteoWeatherIntent()
}

enum class TemperatureUnit(val label: String, val symbol: String) {
	CELSIUS(label = "Celsius", symbol = "°C"), FAHRENHEIT(label = "Fahrenheit", symbol = "°F")
}
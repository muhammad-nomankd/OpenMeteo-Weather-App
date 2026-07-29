package durranitech.openmeteonews.presentation

import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.model.WeatherCondition

internal fun fakeWeather(
	temperature: Double         = 30.0,
	humidity: Int               = 50,
	weatherCode: Int            = 0,
	isDay: Boolean              = true,
	windSpeed: Double           = 15.0,
) = Weather(
	latitude = 34.0,
	longitude = 72.0,
	timezone = "PKT",
	current = CurrentWeather(
		time = "2:00 PM",
		temperature = temperature,
		apparentTemperature = temperature + 3.0,
		humidity = humidity,
		precipitation = 0.0,
		weatherCode = weatherCode,
		condition = WeatherCondition.fromCode(weatherCode, isDay),
		isDay = isDay,
		cloudCover = 10,
		surfacePressure = 1010.0,
		windSpeed = windSpeed,
		windDirection = 270,
		windGusts = windSpeed + 8.0,
	),
	hourly = emptyList(),
	daily = emptyList(),
)
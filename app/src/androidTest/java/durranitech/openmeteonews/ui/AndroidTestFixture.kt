package durranitech.openmeteonews.ui

import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.DailyForecast
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.model.WeatherCondition


fun fakeWeatherUi(temperature: Double = 30.0) = Weather(
	latitude = 34.0,
	longitude = 72.0,
	timezone = "PKT",
	current = CurrentWeather(
		time = "2:00 PM",
		temperature = temperature,
		apparentTemperature = temperature + 3.0,
		humidity = 50,
		precipitation = 0.0,
		weatherCode = 0,
		condition = WeatherCondition.ClearDay,
		isDay = true,
		cloudCover = 10,
		surfacePressure = 1010.0,
		windSpeed = 15.0,
		windDirection = 270,
		windGusts = 22.0,
	),
	hourly = emptyList(),
	daily = listOf(
		DailyForecast(
			date = "2024-07-01",
			dayLabel = "Today",
			weatherCode = 0,
			condition = WeatherCondition.ClearDay,
			tempMax = 35.0,
			tempMin = 22.0,
			sunrise = "5:20 AM",
			sunset = "7:45 PM",
			precipitationSum = 0.0,
			precipitationProbabilityMax = 0,
			windSpeedMax = 20.0,
			uvIndexMax = 6.0,
		),
	),
)

fun fakeDailyForecast(
	dayLabel: String = "Today",
	date: String = "2024-07-01",
	condition: WeatherCondition = WeatherCondition.ClearDay,
	tempMax: Double = 36.0,
	tempMin: Double = 22.0,
	sunrise: String = "5:20 AM",
	sunset: String = "7:45 PM",
	precipitationSum: Double = 0.0,
	precipitationProbabilityMax: Int = 10,
	windSpeedMax: Double = 20.0,
	uvIndexMax: Double = 5.0,
) = DailyForecast(
	date = date,
	dayLabel = dayLabel,
	weatherCode = 0,
	condition = condition,
	tempMax = tempMax,
	tempMin = tempMin,
	sunrise = sunrise,
	sunset = sunset,
	precipitationSum = precipitationSum,
	precipitationProbabilityMax = precipitationProbabilityMax,
	windSpeedMax = windSpeedMax,
	uvIndexMax = uvIndexMax,
)

fun fakeCurrentWeather(
	temperature: Double = 30.0,
) = CurrentWeather(
	time = "2:00 PM",
	temperature = temperature,
	apparentTemperature = 33.0,
	humidity = 50,
	precipitation = 0.0,
	weatherCode = 0,
	condition = WeatherCondition.ClearDay,
	isDay = true,
	cloudCover = 10,
	surfacePressure = 1010.0,
	windSpeed = 15.0,
	windDirection = 270,
	windGusts = 22.0,
)
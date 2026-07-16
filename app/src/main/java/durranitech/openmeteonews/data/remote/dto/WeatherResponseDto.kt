package durranitech.openmeteonews.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
	val longitude: Double,
	val latitude: Double,
	val elevation: Double,
	val timezone: String,
	@SerialName("timezone_abbreviation") val timezoneAbbreviation: String,
	@SerialName("utc_offset_seconds") val utcoffsetSeconds: String,
	@SerialName("current") val current: CurrenWeatherDto,
	@SerialName("hourly") val hourly: HourlyWeatherDto,
	@SerialName("daily") val daily: DailyWeatherDto
)
@Serializable
data class HourlyWeatherDto(
	val time: List<String>,
	@SerialName("temperature_2m") val temperature: List<Double>,
	@SerialName("precipitation_probability") val precipitationProbability: List<Int>,
	@SerialName("weather_code") val weatherCode: List<Int>,
	@SerialName("wind_speed_10m") val windSpeed: List<Double>,
	@SerialName("relative_humidity_2m") val humidity: List<Int>,
)

@Serializable
data class DailyWeatherDto(
	val time: List<String>,
	@SerialName("weather_code") val weatherCode: List<Int>,
	@SerialName("temperature_2m_max") val tempMax: List<Double>,
	@SerialName("temperature_2m_min") val tempMin: List<Double>,
	val sunrise: List<String>,
	val sunset: List<String>,
	@SerialName("precipitation_sum") val precipitationSum: List<Double>,
	@SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
	@SerialName("wind_speed_10m_max") val windSpeedMax: List<Double>,
	@SerialName("uv_index_max") val uvIndexMax: List<Double>,

)
@Serializable
data class CurrenWeatherDto(
	val time: String,
	val interval: Int,
	@SerialName("temperature_2m") val temperature: Double,
	@SerialName("relative_humidity_2m") val humidity: Int,
	@SerialName("apparent_temperature") val apparentTemperature: Double,
	@SerialName("is_day") val isDay: Int,
	val precipitation: Double,
	val rain: Double,
	val showers: Double,
	val snowfall: Double,
	@SerialName("weather_code") val weatherCode: Int,
	@SerialName("cloud_cover") val cloudCover: Int,
	@SerialName("surface_pressure") val surfacePressure: Double,
	@SerialName("wind_speed_10m") val windSpeed: Double,
	@SerialName("wind_direction_10m") val windDirection: Int,
	@SerialName("wind_gusts_10m") val windGusts: Double)
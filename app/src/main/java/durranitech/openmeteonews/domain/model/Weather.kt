package durranitech.openmeteonews.domain.model
import androidx.compose.runtime.Immutable

@Immutable
data class Weather(
	val latitude: Double,
	val longitude: Double,
	val timezone: String,
	val current: CurrentWeather,
	val hourly: List<HourlyWeather>,
	val daily: List<DailyForecast>,
)

@Immutable
data class CurrentWeather(
	val time: String,
	val temperature: Double,
	val apparentTemperature: Double,
	val humidity: Int,
	val precipitation: Double,
	val weatherCode: Int,
	val condition: WeatherCondition,
	val isDay: Boolean,
	val cloudCover: Int,
	val surfacePressure: Double,
	val windSpeed: Double,
	val windDirection: Int,
	val windGusts: Double,
)

@Immutable
data class HourlyWeather(
	val time: String,
	val temperature: Double,
	val precipitationProbability: Int,
	val weatherCode: Int,
	val condition: WeatherCondition,
	val windSpeed: Double,
	val humidity: Int,
)

@Immutable
data class DailyForecast(
	val date: String,
	val dayLabel: String,
	val weatherCode: Int,
	val condition: WeatherCondition,
	val tempMax: Double,
	val tempMin: Double,
	val sunrise: String,
	val sunset: String,
	val precipitationSum: Double,
	val precipitationProbabilityMax: Int,
	val windSpeedMax: Double,
	val uvIndexMax: Double,
)

sealed class WeatherCondition(
	val label: String,
	val emoji: String,
	val backgroundStart: Long,
	val backgroundEnd: Long,
) {
	data object ClearDay : WeatherCondition("Clear Sky", "☀️", 0xFF1A73E8, 0xFF0D47A1)
	data object ClearNight : WeatherCondition("Clear Night", "🌙", 0xFF0D1B3E, 0xFF1A237E)
	data object MainlyClear : WeatherCondition("Mainly Clear", "🌤️", 0xFF29B6F6, 0xFF0288D1)
	data object PartlyCloudy : WeatherCondition("Partly Cloudy", "⛅", 0xFF546E7A, 0xFF37474F)
	data object Overcast : WeatherCondition("Overcast", "☁️", 0xFF607D8B, 0xFF455A64)
	data object Fog : WeatherCondition("Foggy", "🌫️", 0xFF78909C, 0xFF546E7A)
	data object Drizzle : WeatherCondition("Drizzle", "🌦️", 0xFF4FC3F7, 0xFF0277BD)
	data object FreezingDrizzle : WeatherCondition("Freezing Drizzle", "🌧️", 0xFF4DD0E1, 0xFF00838F)
	data object RainSlight : WeatherCondition("Light Rain", "🌧️", 0xFF1565C0, 0xFF0D47A1)
	data object RainModerate : WeatherCondition("Moderate Rain", "🌧️", 0xFF1E88E5, 0xFF1565C0)
	data object RainHeavy : WeatherCondition("Heavy Rain", "⛈️", 0xFF0D47A1, 0xFF01579B)
	data object FreezingRain : WeatherCondition("Freezing Rain", "🌨️", 0xFF26C6DA, 0xFF0097A7)
	data object SnowSlight : WeatherCondition("Light Snow", "🌨️", 0xFFB3E5FC, 0xFF4FC3F7)
	data object SnowModerate : WeatherCondition("Moderate Snow", "❄️", 0xFF81D4FA, 0xFF29B6F6)
	data object SnowHeavy : WeatherCondition("Heavy Snow", "☃️", 0xFFE1F5FE, 0xFF81D4FA)
	data object SnowGrains : WeatherCondition("Snow Grains", "🌨️", 0xFFB2EBF2, 0xFF80DEEA)
	data object RainShowers : WeatherCondition("Rain Showers", "🌦️", 0xFF1976D2, 0xFF0D47A1)
	data object SnowShowers : WeatherCondition("Snow Showers", "🌨️", 0xFF80DEEA, 0xFF4DD0E1)
	data object Thunderstorm : WeatherCondition("Thunderstorm", "⛈️", 0xFF263238, 0xFF1A237E)
	data object ThunderstormHail :
		WeatherCondition("Thunderstorm+Hail", "⛈️", 0xFF1A237E, 0xFF0D0D0D)

	data object Unknown : WeatherCondition("Unknown", "🌡️", 0xFF455A64, 0xFF263238)

	companion object {
		fun fromCode(code: Int, isDay: Boolean = true): WeatherCondition = when (code) {
			0 -> if (isDay) ClearDay else ClearNight
			1, 2 -> MainlyClear
			3 -> Overcast
			45, 48 -> Fog
			51, 53, 55 -> Drizzle
			56, 57 -> FreezingDrizzle
			61 -> RainSlight
			63 -> RainModerate
			65 -> RainHeavy
			66, 67 -> FreezingRain
			71 -> SnowSlight
			73 -> SnowModerate
			75 -> SnowHeavy
			77 -> SnowGrains
			80, 81, 82 -> RainShowers
			85, 86 -> SnowShowers
			95 -> Thunderstorm
			96, 99 -> ThunderstormHail
			else -> Unknown
		}
	}
}

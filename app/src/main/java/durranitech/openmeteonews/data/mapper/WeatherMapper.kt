package durranitech.openmeteonews.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.DailyForecast
import durranitech.openmeteonews.domain.model.HourlyWeather
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.model.WeatherCondition
import durranitech.openmeteonews.data.remote.dto.WeatherResponseDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
fun WeatherResponseDto.toDomain(): Weather {
	val isDay = current.isDay == 1

	val currentWeather = CurrentWeather(
		time = current.time.formatDisplayTime(),
		temperature = current.temperature,
		apparentTemperature = current.apparentTemperature,
		humidity = current.humidity,
		precipitation = current.precipitation,
		weatherCode = current.weatherCode,
		condition = WeatherCondition.fromCode(current.weatherCode, isDay),
		isDay = isDay,
		cloudCover = current.cloudCover,
		surfacePressure = current.surfacePressure,
		windSpeed = current.windSpeed,
		windDirection = current.windDirection,
		windGusts = current.windGusts,
	)

	// Hourly: Open-Meteo returns 168 hours (7 days). We only show the
	// next 24 from now to keep the UI focused.
	val hourlyItems = hourly.time.indices.map { i ->
		HourlyWeather(
			time = hourly.time[i].formatHourDisplay(),
			temperature = hourly.temperature[i],
			precipitationProbability = hourly.precipitationProbability[i],
			weatherCode = hourly.weatherCode[i],
			condition = WeatherCondition.fromCode(hourly.weatherCode[i]),
			windSpeed = hourly.windSpeed[i],
			humidity = hourly.humidity[i],
		)
	}.take(24)

	val dailyItems = daily.time.indices.map { i ->
		DailyForecast(
			date = daily.time[i],
			dayLabel = daily.time[i].toDayLabel(i),
			weatherCode = daily.weatherCode[i],
			condition = WeatherCondition.fromCode(daily.weatherCode[i]),
			tempMax = daily.tempMax[i],
			tempMin = daily.tempMin[i],
			sunrise = daily.sunrise[i].formatDisplayTime(),
			sunset = daily.sunset[i].formatDisplayTime(),
			precipitationSum = daily.precipitationSum[i],
			precipitationProbabilityMax = daily.precipitationProbabilityMax[i],
			windSpeedMax = daily.windSpeedMax[i],
			uvIndexMax = daily.uvIndexMax[i],
		)
	}

	return Weather(
		latitude = latitude,
		longitude = longitude,
		timezone = timezoneAbbreviation,
		current = currentWeather,
		hourly = hourlyItems,
		daily = dailyItems,
	)
}

// ─── Private helpers ─────────────────────────────────────────────────────────

/** "2024-07-01T14:00" → "2:00 PM" */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.formatDisplayTime(): String = try {
	val dt = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	dt.format(DateTimeFormatter.ofPattern("h:mm a"))
} catch (_: Exception) { this }

/** "2024-07-01T14:00" → "2 PM" (compact for the hourly strip) */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.formatHourDisplay(): String = try {
	val dt = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	dt.format(DateTimeFormatter.ofPattern("h a"))
} catch (_: Exception) { this }

/** "2024-07-01" → "Today" / "Mon" / "Tue" etc. */
@RequiresApi(Build.VERSION_CODES.O)
private fun String.toDayLabel(index: Int): String = when (index) {
	0    -> "Today"
	1    -> "Tomorrow"
	else -> try {
		val date = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
		date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
	} catch (_: Exception) { this }
}
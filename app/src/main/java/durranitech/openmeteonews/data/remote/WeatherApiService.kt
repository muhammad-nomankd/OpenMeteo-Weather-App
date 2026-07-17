package durranitech.openmeteonews.data.remote

import durranitech.openmeteonews.data.remote.dto.WeatherResponseDto
import durranitech.openmeteonews.domain.model.CurrentWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

	@GET("v1/forecast")
	suspend fun getForecast(
		@Query("latitude") latitude: Double,
		@Query("longitude") longitude: Double,
		@Query("current") current:String = CURRENT_VARIABLES,
		@Query("hourly") hourly: String = HOURLY_VARIABLES,
		@Query("daily") daily: String=DAILY_VARIABLES,
		@Query("timezone") timezone: String = "auto",
		@Query("forecast_days") forecastDays: Int = 7,
		@Query("wind_speed_unit") windSpeedUnit: String = "kmh"
	): WeatherResponseDto

	companion object {
		const val CURRENT_VARIABLES =
			"temperature_2m,relative_humidity_2m,apparent_temperature," +
					"is_day,precipitation,rain,showers,snowfall,weather_code," +
					"cloud_cover,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m"

		const val HOURLY_VARIABLES =
			"temperature_2m,precipitation_probability,weather_code,wind_speed_10m,relative_humidity_2m"

		const val DAILY_VARIABLES =
			"weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset," +
					"precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max"
	}
}
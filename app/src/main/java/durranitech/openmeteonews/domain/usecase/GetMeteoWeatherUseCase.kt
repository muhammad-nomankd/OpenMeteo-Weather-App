package durranitech.openmeteonews.domain.usecase

import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.repository.OpenMeteoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeteoWeatherUseCase @Inject constructor(private val repository: OpenMeteoRepository) {
	operator fun invoke(latitude: Double, longitude: Double): Flow<AppResult<Weather>> {
		require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
		require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
		return repository.getForecast(latitude, longitude)
	}
}
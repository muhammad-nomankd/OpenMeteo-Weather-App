package durranitech.openmeteonews.domain.repository

import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface  OpenMeteoRepository {

	fun getForecast(latitude: Double, longitude: Double): Flow<AppResult<Weather>>

}
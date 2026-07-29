package durranitech.openmeteonews.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.data.mapper.toDomain
import durranitech.openmeteonews.data.remote.WeatherApiService
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.repository.OpenMeteoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
class OpenMeteoRepositoryImp @Inject constructor(private val apiService: WeatherApiService) :
	OpenMeteoRepository {
	@RequiresApi(Build.VERSION_CODES.O)
	override fun getForecast(
		latitude: Double, longitude: Double
	): Flow<AppResult<Weather>> = flow {
		emit(AppResult.Loading)
		try {
			val dto = apiService.getForecast(
				latitude = latitude, longitude = longitude
			)
			emit(AppResult.Success(
				data = dto.toDomain()
			))
		} catch (e: UnknownHostException) {
			emit(AppResult.Error("No internet connection. Check your network", e))
		} catch (e: IOException) {
			emit(AppResult.Error("Network Error please retry",e))
		} catch (e: HttpException) {
			val message = when (e.code()) {
				400 -> "Bad request - check coordinates."
				408 -> "Request time out"
				429 -> "Rate limit exceeded"
				500 -> "Server error"
				503 -> "Service unavailable"
				504 -> "Gateway timeout"
				521 -> "Web server is down"
				522 -> "Connection timed out"
				524 -> "A timeout occurred"
				else -> "Unknown error"
			}
			emit(AppResult.Error(message, e))
		} catch (e: Exception) {
			emit(AppResult.Error("Unknown Error", e))
		}
	}.flowOn(Dispatchers.IO)
}
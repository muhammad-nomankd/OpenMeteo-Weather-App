package durranitech.openmeteonews.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import durranitech.openmeteonews.core.AppResult
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.domain.usecase.GetMeteoWeatherUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenMeteoWeatherViewModel @Inject constructor(private val getOpenMeteoWeatherUseCase: GetMeteoWeatherUseCase) :
	ViewModel() {


	private val _uiState = MutableStateFlow(OpenMeteoWeatherUiState())
	val uiState: StateFlow<OpenMeteoWeatherUiState> = _uiState.asStateFlow()


	private var fetchJob: Job? = null


	fun onIntent(intent: OpenMeteoWeatherIntent) {
		when (intent) {
			is OpenMeteoWeatherIntent.LoadWeather -> fetchWeather(
				latitude = intent.latitude,
				longitude = intent.longitude,
				isRefresh = false
			)

			is OpenMeteoWeatherIntent.RefreshWeather -> fetchWeather(
				latitude = intent.latitude,
				longitude = intent.longitude,
				isRefresh = true
			)

			is OpenMeteoWeatherIntent.ChangeTemperatureUnit -> _uiState.update { it.copy(unit = intent.unit) }
			is OpenMeteoWeatherIntent.DismissError -> _uiState.update { it.copy(errorMessage = null) }
		}
	}

	private fun fetchWeather(latitude: Double, longitude: Double, isRefresh: Boolean) {
		fetchJob?.cancel()
		fetchJob = viewModelScope.launch {
			getOpenMeteoWeatherUseCase(latitude, longitude).collect { result ->
				_uiState.update { it.update(result, isRefresh) }
			}
		}
	}
}

fun OpenMeteoWeatherUiState.update(
	result: AppResult<Weather>, isRefresh: Boolean
): OpenMeteoWeatherUiState = when (result) {
	is AppResult.Loading -> copy(
		isLoading = !isRefresh,
		isRefreshing = isRefresh,
		errorMessage = null,
	)

	is AppResult.Success -> copy(
		isLoading = false,
		isRefreshing = false,
		weather = result.data,
		errorMessage = null,
	)

	is AppResult.Error -> copy(
		isLoading = false,
		isRefreshing = false,
		errorMessage = result.message,
	)
}

package durranitech.openmeteonews.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import durranitech.openmeteonews.presentation.OpenMeteoDailyForecastScreen
import durranitech.openmeteonews.presentation.OpenMeteoWeatherViewModel
import durranitech.openmeteonews.presentation.WeatherHomeScreen

@Composable
fun AppNavigation() {
	val backStack = rememberNavBackStack(Destination.HomeDestination)

	NavDisplay(
		backStack = backStack,
		onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
		entryProvider = entryProvider {
			entry<Destination.HomeDestination> {
				val viewModel: OpenMeteoWeatherViewModel = hiltViewModel()
				val uiState = viewModel.uiState.collectAsStateWithLifecycle()
				val startLat = 34.0027214159725
				val startLon = 72.15057570503163


				WeatherHomeScreen(
					uiState = uiState.value,
					onIntent = viewModel::onIntent,
					startLat = startLat,
					startLon = startLon,
					onDayClick = { index ->
						backStack.add(Destination.DailyDetailDestination(index, startLat, startLon))
					},
					modifier = Modifier.fillMaxSize(),
				)
			}

			entry<Destination.DailyDetailDestination> { des ->
				val viewModel: OpenMeteoWeatherViewModel = hiltViewModel()
				val uiState = viewModel.uiState.collectAsStateWithLifecycle()
				val dailyWeather = uiState.value.weather?.daily?.getOrNull(des.dailyIndex)
				if (dailyWeather != null) {
					OpenMeteoDailyForecastScreen(
						day = dailyWeather,
						unit = uiState.value.unit,
						onBack = { backStack.removeLastOrNull() },
						current = uiState.value.weather!!.current,
						modifier = Modifier.fillMaxSize(),
					)
				}

			}
		}

	)
}
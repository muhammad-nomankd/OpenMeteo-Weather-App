package durranitech.openmeteonews.presentation
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durranitech.openmeteonews.presentation.components.WeatherStatCard
import durranitech.openmeteonews.domain.model.WeatherCondition
import durranitech.openmeteonews.presentation.components.HourlyForecastRow
import durranitech.openmeteonews.presentation.components.OpenMeteoDailyForecastItem
import durranitech.openmeteonews.presentation.components.WeatherScreenSkeleton
import androidx.compose.ui.tooling.preview.Preview
import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.DailyForecast
import durranitech.openmeteonews.domain.model.HourlyWeather
import durranitech.openmeteonews.domain.model.Weather
import durranitech.openmeteonews.ui.theme.OpenMeteoNewsTheme


@Composable
fun WeatherHomeScreen(
	uiState: OpenMeteoWeatherUiState,
	onIntent: (OpenMeteoWeatherIntent) -> Unit,
	startLat: Double,
	startLon: Double,
	onDayClick: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(startLat, startLon) {
		onIntent(OpenMeteoWeatherIntent.LoadWeather(startLat, startLon))
	}
	

	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(uiState.errorMessage) {
		if (uiState.errorMessage != null && uiState.hasData) {
			snackbarHostState.showSnackbar(uiState.errorMessage)
			onIntent(OpenMeteoWeatherIntent.DismissError)
		}
	}

	val (gradientStart, gradientEnd) = remember(uiState.weather?.current?.condition) {
		val cond = uiState.weather?.current?.condition ?: WeatherCondition.ClearDay
		Color(cond.backgroundStart) to Color(cond.backgroundEnd)
	}

	val animatedStart by animateColorAsState(
		targetValue = gradientStart,
		animationSpec = spring(),
		label = "gradientStart",
	)
	val animatedEnd by animateColorAsState(
		targetValue = gradientEnd,
		animationSpec = spring(),
		label = "gradientEnd",
	)

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(Brush.verticalGradient(listOf(animatedStart, animatedEnd))),
	) {
		PullToRefreshBox(
			isRefreshing = uiState.isRefreshing == true,
			onRefresh = { onIntent(OpenMeteoWeatherIntent.RefreshWeather(startLat, startLon)) },
			modifier = Modifier.fillMaxSize(),
		) {
			AnimatedContent(
				targetState = uiState.isInitialLoading,
				transitionSpec = {
					fadeIn(spring()) togetherWith fadeOut(spring())
				},
				label = "loadingTransition",
			) { isLoading ->
				if (isLoading) {
					WeatherScreenSkeleton(
						modifier = Modifier.fillMaxSize().statusBarsPadding(),
					)
				} else if (uiState.isTerminalError) {
					TerminalErrorState(
						message = uiState.errorMessage ?: "Unknown error",
						onRetry = { onIntent(OpenMeteoWeatherIntent.LoadWeather(startLat, startLon)) },
						modifier = Modifier.fillMaxSize(),
					)
				} else if (uiState.hasData) {
					WeatherContent(
						uiState = uiState,
						onDayClick = onDayClick,
					)
				}
			}
		}

		if (uiState.hasData) {
			UnitToggle(
				unit = uiState.unit,
				onToggle = {
					val next = if (uiState.unit == TemperatureUnit.CELSIUS)
						TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
					onIntent(OpenMeteoWeatherIntent.ChangeTemperatureUnit(next))
				},
				modifier = Modifier
					.align(Alignment.TopEnd)
					.statusBarsPadding()
					.padding(end = 16.dp, top = 8.dp),
			)
		}

		SnackbarHost(
			hostState = snackbarHostState,
			modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
		)
	}
}


@Composable
private fun WeatherContent(
	uiState: OpenMeteoWeatherUiState,
	onDayClick: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {

	var visible by remember { mutableStateOf(false) }
	LaunchedEffect(Unit) {
		visible = true
	}
	val weather = uiState.weather!!
	val current = weather.current


	val animatedTemp by animateFloatAsState(
		targetValue = if (visible)current.temperature.toFloat() else 0f,
		animationSpec = spring(Spring.DampingRatioNoBouncy),
		label = "temperature",
	)

	LazyColumn(
		modifier = modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		item {
			Spacer(Modifier.height(56.dp))
			AnimatedVisibility(
				visible = visible,
				enter = fadeIn(spring()) + slideInVertically(spring()) { it / 4 },
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(4.dp),
					) {
						Icon(
							imageVector = Icons.Default.LocationOn,
							contentDescription = null,
							tint = Color.White.copy(alpha = 0.8f),
							modifier = Modifier.size(16.dp),
						)
						Text(
							text = uiState.locationLabel?:"",
							style = MaterialTheme.typography.titleMedium,
							color = Color.White.copy(alpha = 0.9f),
						)
					}

					Spacer(Modifier.height(4.dp))

					Text(
						text = animatedTemp.toDouble().formatTemp(uiState.unit),
						fontSize = 80.sp,
						fontWeight = FontWeight.Thin,
						color = Color.White,
						lineHeight = 80.sp,
						modifier = Modifier.semantics {
							contentDescription =
								"${current.temperature.toInt()}${uiState.unit.symbol}, ${current.condition.label}"
							liveRegion = LiveRegionMode.Polite
						},
					)

					Text(text = current.condition.emoji, fontSize = 40.sp)
					Spacer(Modifier.height(6.dp))
					Text(
						text = current.condition.label,
						style = MaterialTheme.typography.titleLarge,
						color = Color.White,
					)
					Text(
						text = "Feels like ${current.apparentTemperature.formatTemp(uiState.unit)}",
						style = MaterialTheme.typography.bodyMedium,
						color = Color.White.copy(alpha = 0.7f),
					)
				}
			}
		}

		item {
			Spacer(Modifier.height(32.dp))
			Row(
				modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				WeatherStatCard(
					icon = "💨", label = "Wind",
					value = "${current.windSpeed.toInt()} km/h",
					modifier = Modifier.weight(1f),
				)
				WeatherStatCard(
					icon = "💧", label = "Humidity",
					value = "${current.humidity}%",
					modifier = Modifier.weight(1f),
				)
				WeatherStatCard(
					icon = "🌡️", label = "Pressure",
					value = "${current.surfacePressure.toInt()} hPa",
					modifier = Modifier.weight(1f),
				)
			}
		}

		item {
			Spacer(Modifier.height(28.dp))
			SectionHeader(title = "Hourly Forecast")
			Spacer(Modifier.height(12.dp))
		}

		item { HourlyForecastRow(hourly = weather.hourly, unit = uiState.unit) }

		item {
			Spacer(Modifier.height(28.dp))
			SectionHeader(title = "7-Day Forecast", modifier = Modifier.padding(horizontal = 24.dp))
			Spacer(Modifier.height(8.dp))
		}

		itemsIndexed(items = weather.daily, key = { _, day -> day.date }) { index, day ->
			OpenMeteoDailyForecastItem(
				day = day,
				unit = uiState.unit,
				onClick = { onDayClick(index) },
				modifier = Modifier
					.animateItem()
					.padding(horizontal = 16.dp, vertical = 2.dp),
			)
		}

		weather.daily.firstOrNull()?.let { today ->
			item {
				Spacer(Modifier.height(20.dp))
				Row(
					modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
					horizontalArrangement = Arrangement.spacedBy(10.dp),
				) {
					WeatherStatCard(
						icon = "🌅", label = "Sunrise", value = today.sunrise,
						modifier = Modifier.weight(1f),
					)
					WeatherStatCard(
						icon = "🌇", label = "Sunset", value = today.sunset,
						modifier = Modifier.weight(1f),
					)
					WeatherStatCard(
						icon = "☀️", label = "UV Index", value = today.uvIndexMax.toString(),
						modifier = Modifier.weight(1f),
					)
				}
				Spacer(Modifier.height(40.dp))
			}
		}
	}
}

// ─── Terminal error (no existing data to fall back on) ───────────────────────

@Composable
private fun TerminalErrorState(
	message: String,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Text(text = "⚠️", fontSize = 56.sp)
		Spacer(Modifier.height(16.dp))
		Text(
			text = message,
			style = MaterialTheme.typography.bodyLarge,
			color = Color.White,
			textAlign = TextAlign.Center,
		)
		Spacer(Modifier.height(24.dp))
		Button(
			onClick = onRetry,
			colors = ButtonDefaults.buttonColors(
				containerColor = Color.White.copy(alpha = 0.25f),
				contentColor = Color.White,
			),
			shape = MaterialTheme.shapes.medium,
		) {
			Icon(Icons.Default.Refresh, contentDescription = null)
			Text(text = "  Retry", style = MaterialTheme.typography.labelLarge)
		}
	}
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
	Text(
		text = title,
		style = MaterialTheme.typography.titleSmall,
		fontWeight = FontWeight.SemiBold,
		color = Color.White.copy(alpha = 0.7f),
		modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
	)
}

@Composable
private fun UnitToggle(
	unit: TemperatureUnit,
	onToggle: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val label = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F"
	IconButton(
		onClick = onToggle,
		modifier = modifier.semantics {
			contentDescription = "Switch to ${if (unit == TemperatureUnit.CELSIUS) "Fahrenheit" else "Celsius"}"
		},
	) {
		Text(text = label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
	}
}

@Preview(showBackground = true)
@Composable
private fun WeatherHomeScreenPreview() {
	val mockWeather = Weather(
		latitude = 34.01,
		longitude = 71.97,
		timezone = "GMT",
		current = CurrentWeather(
			time = "7pm",
			temperature = 25.5,
			apparentTemperature = 27.0,
			humidity = 45,
			precipitation = 0.0,
			weatherCode = 0,
			condition = WeatherCondition.RainShowers,
			isDay = true,
			cloudCover = 10,
			surfacePressure = 1013.0,
			windSpeed = 12.0,
			windDirection = 180,
			windGusts = 15.0
		),
		hourly = List(24) { hour ->
			HourlyWeather(
				time = "2023-10-10T${hour.toString().padStart(2, '0')}:00",
				temperature = 20.0 + hour % 10,
				precipitationProbability = hour * 2 % 100,
				weatherCode = 1,
				condition = WeatherCondition.MainlyClear,
				windSpeed = 10.0,
				humidity = 50
			)
		},
		daily = List(7) { day ->
			DailyForecast(
				date = "2023-10-1${0 + day}",
				dayLabel = when (day) {
					0 -> "Today"
					1 -> "Mon"
					2 -> "Tue"
					3 -> "Wed"
					4 -> "Thu"
					5 -> "Fri"
					else -> "Sat"
				},
				weatherCode = 0,
				condition = if (day % 2 == 0) WeatherCondition.ClearDay else WeatherCondition.PartlyCloudy,
				tempMax = 28.0 - day,
				tempMin = 18.0 + day,
				sunrise = "06:15",
				sunset = "18:45",
				precipitationSum = 0.0,
				precipitationProbabilityMax = 10 * day,
				windSpeedMax = 15.0,
				uvIndexMax = 6.0
			)
		}
	)

	val mockUiState = OpenMeteoWeatherUiState(
		isLoading = false,
		weather = mockWeather,
		unit = TemperatureUnit.CELSIUS,
		locationLabel = "Akora Khattak, Nowshera"
	)

	OpenMeteoNewsTheme {
		WeatherHomeScreen(
			uiState = mockUiState,
			onIntent = {},
			startLat = 34.01,
			startLon = 71.97,
			onDayClick = {}
		)
	}
}

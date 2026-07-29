package durranitech.openmeteonews.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durranitech.openmeteonews.domain.model.CurrentWeather
import durranitech.openmeteonews.domain.model.DailyForecast


@Composable
fun OpenMeteoDailyForecastScreen(
	day: DailyForecast,
	unit: TemperatureUnit,
	onBack: () -> Unit,
	current: CurrentWeather,
	modifier: Modifier = Modifier,
) {

	val textColor by animateColorAsState(
		targetValue = Color.Red
	)
	// Stagger-in animation: trigger once when screen enters composition
	var visible by remember { mutableStateOf(false) }
	LaunchedEffect(Unit) { visible = true }

	// Animated temperature values for smooth count-up on entry
	val animatedMax by animateFloatAsState(
		targetValue = if (visible) day.tempMax.toFloat() else day.tempMin.toFloat(),
		animationSpec = tween(2000),
		label = "tempMax",
	)

	val animatedMin by animateFloatAsState(
		targetValue = if (visible) day.tempMin.toFloat() else day.tempMin.toFloat(),
		animationSpec = tween(2000),
		label = "tempMin",
	)

	val animatedCurrentTemp by animateFloatAsState(
		targetValue = if (visible) current.temperature.toFloat() else day.tempMin.toFloat(),
		animationSpec = tween(2000),
		label = "currentTemp",
	)

	// Dynamic gradient from the condition's palette
	val gradientStart = Color(day.condition.backgroundStart)
	val gradientEnd = Color(day.condition.backgroundEnd)

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(Brush.verticalGradient(listOf(gradientStart, gradientEnd))),
	) {
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {

			// ── Top bar ────────────────────────────────────────────────────
			item {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 8.dp, vertical = 4.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
							tint = Color.White,
						)
					}
					Text(
						text = day.dayLabel,
						style = MaterialTheme.typography.titleLarge,
						color = Color.White,
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier.weight(1f),
						textAlign = TextAlign.Center,
					)

					// Balance the back button so the title is truly centred
					Spacer(Modifier.size(48.dp))
				}
			}

			item {
				AnimatedVisibility(
					visible = visible,
					enter = fadeIn(tween(2000)) + slideInVertically(tween(2000)) { it / 3 },
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						modifier = Modifier.padding(top = 16.dp),
					) {
						Text(text = day.condition.emoji, fontSize = 72.sp)
						Spacer(Modifier.height(8.dp))
						Text(
							text = day.condition.label,
							style = MaterialTheme.typography.titleLarge,
							color = Color.White,
						)
						Text(
							text = day.date,
							style = MaterialTheme.typography.bodySmall,
							color = Color.White.copy(alpha = 0.6f),
						)
					}
				}
			}

			// ── Temperature range arc ──────────────────────────────────────
			item {
				Spacer(Modifier.height(28.dp))
				AnimatedVisibility(
					visible = visible,
					enter = fadeIn(tween(2000)),
				) {
					TempRangeArc(
						tempMax = animatedMax.toDouble(),
						tempMin = animatedMin.toDouble(),
						tempCurrent = animatedCurrentTemp.toDouble(),
						unit = unit,
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 48.dp),
					)
				}
			}


			// ── Stats grid ─────────────────────────────────────────────────
			item {
				Spacer(Modifier.height(28.dp))
				AnimatedVisibility(
					visible = visible,
					enter = fadeIn(tween(5000)) + slideInVertically(tween(2000)) { it / 4 },
				) {
					DetailStatsGrid(day = day, modifier = Modifier.padding(horizontal = 20.dp))
				}
			}

			// ── Sunrise / Sunset ───────────────────────────────────────────
			item {
				Spacer(Modifier.height(20.dp))
				AnimatedVisibility(
					visible = visible,
					enter = fadeIn(tween(2000)) + slideInVertically(tween(2000)) { it / 4 },
				) {
					SunriseSunsetBar(
						sunrise = day.sunrise,
						sunset = day.sunset,
						modifier = Modifier.padding(horizontal = 20.dp),
					)
				}
			}

			// ── UV Index bar ───────────────────────────────────────────────
			item {
				Spacer(Modifier.height(16.dp))
				AnimatedVisibility(
					visible = visible,
					enter = fadeIn(tween(1000)),
				) {
					UvIndexBar(
						uvIndex = day.uvIndexMax,
						modifier = Modifier
							.padding(horizontal = 20.dp),
					)
				}
				Spacer(Modifier.height(40.dp))
			}
		}
	}
}

// ─── Temperature range arc ─────────────────────────────────────────────────

@Composable
private fun TempRangeArc(
	tempMax: Double,
	tempMin: Double,
	unit: TemperatureUnit,
	modifier: Modifier = Modifier,
	tempCurrent: Double,
) {
	val coldColor = Color(0xFF90CAF9)
	val hotColor = Color(0xFFFF7043)

	Box(
		modifier = modifier
			.height(120.dp)
			.fillMaxWidth()
			.drawBehind {
				drawTempArc(
					tempMin = tempMin,
					tempMax = tempMax,
					coldColor = coldColor,
					hotColor = hotColor,
					tempCurrent = tempCurrent,
				)
			},
		contentAlignment = Alignment.Center,
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(20.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text(
						text = " Low",
						style = MaterialTheme.typography.labelSmall,
						color = coldColor,
						modifier = Modifier.testTag("TempLevel")
					)
					Text(
						text = tempMin.formatTemp(unit),
						fontSize = 28.sp,
						fontWeight = FontWeight.Light,
						color = coldColor,
					)
				}
				Text(
					text = "—",
					color = Color.White.copy(alpha = 0.4f),
					fontSize = 20.sp,
				)
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text(
						text = "High",
						style = MaterialTheme.typography.labelSmall,
						color = hotColor,
					)
					Text(
						text = tempMax.formatTemp(unit),
						fontSize = 28.sp,
						fontWeight = FontWeight.Light,
						color = hotColor,
					)
				}
			}
		}
	}
}

/** Canvas drawing of the arc behind the temperature numbers. */
private fun DrawScope.drawTempArc(
	tempMin: Double,
	tempMax: Double,
	tempCurrent: Double,
	coldColor: Color,
	hotColor: Color,
) {
	val strokeWidth = 6.dp.toPx()
	val padding = strokeWidth / 2
	val arcRect = Rect(
		offset = Offset(padding, padding),
		size = Size(size.width - padding * 2, (size.height - padding) * 2),
	)
	val range = (tempMax - tempMin).coerceAtLeast(1.0) // avoid divide by zero
	val filledSweep = ((tempCurrent - tempMin) / range * 180.0).toFloat().coerceIn(0f, 180f)
	// Background track
	drawArc(
		color = Color.White.copy(alpha = 0.1f),
		startAngle = 180f,
		sweepAngle = 180f,
		useCenter = false,
		topLeft = arcRect.topLeft,
		size = arcRect.size,
		style = Stroke(strokeWidth, cap = StrokeCap.Round),
	)
	// Filled arc using gradient brush
	drawArc(
		brush = Brush.horizontalGradient(listOf(coldColor, hotColor)),
		startAngle = 180f,
		sweepAngle = filledSweep,
		useCenter = false,
		topLeft = arcRect.topLeft,
		size = arcRect.size,
		style = Stroke(strokeWidth, cap = StrokeCap.Round),
	)
}

// ─── Stats grid ────────────────────────────────────────────────────────────

@Composable
private fun DetailStatsGrid(
	day: DailyForecast,
	modifier: Modifier = Modifier,
) {
	val items = listOf(
		Triple("💨", "Max Wind", "${day.windSpeedMax.toInt()} km/h"),
		Triple("🌧️", "Precipitation", "${day.precipitationSum} mm"),
		Triple("💧", "Rain Chance", "${day.precipitationProbabilityMax}%"),
		Triple("☀️", "UV Index", day.uvIndexMax.toString()),
	)

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		// Two items per row
		items.chunked(2).forEach { rowItems ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				rowItems.forEach { (icon, label, value) ->
					DetailStatTile(
						icon = icon,
						label = label,
						value = value,
						modifier = Modifier.weight(1f),
					)
				}
				// If odd number of items, fill remaining space
				if (rowItems.size == 1) Spacer(Modifier.weight(1f))
			}
		}
	}
}

@Composable
private fun DetailStatTile(
	icon: String,
	label: String,
	value: String,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(Color.White.copy(alpha = 0.12f))
			.padding(horizontal = 16.dp, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Text(text = icon, fontSize = 26.sp)
		Column {
			Text(
				text = label,
				style = MaterialTheme.typography.labelSmall,
				color = Color.White.copy(alpha = 0.65f),
			)
			Text(
				text = value,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.SemiBold,
				color = Color.White,
			)
		}
	}
}

// ─── Sunrise / Sunset card ─────────────────────────────────────────────────

@Composable
private fun SunriseSunsetBar(
	sunrise: String,
	sunset: String,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(Color.White.copy(alpha = 0.12f))
			.padding(horizontal = 20.dp, vertical = 16.dp),
	) {
		Text(
			text = "Sun",
			style = MaterialTheme.typography.labelSmall,
			color = Color.White.copy(alpha = 0.65f),
			fontWeight = FontWeight.SemiBold,
		)
		Spacer(Modifier.height(12.dp))
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			SunTimeItem(emoji = "🌅", label = "Sunrise", time = sunrise)
			// Simple visual arc between the two
			Text(text = "──────", color = Color.White.copy(alpha = 0.2f), fontSize = 10.sp)
			Text(text = "☀️", fontSize = 22.sp)
			Text(text = "──────", color = Color.White.copy(alpha = 0.2f), fontSize = 10.sp)
			SunTimeItem(emoji = "🌇", label = "Sunset", time = sunset)
		}
	}
}

@Composable
private fun SunTimeItem(emoji: String, label: String, time: String) {
	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(text = emoji, fontSize = 26.sp)
		Spacer(Modifier.height(4.dp))
		Text(
			text = time,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold,
			color = Color.White,
		)
		Text(
			text = label,
			style = MaterialTheme.typography.labelSmall,
			color = Color.White.copy(alpha = 0.6f),
		)
	}
}

// ─── UV Index bar ──────────────────────────────────────────────────────────

@Composable
private fun UvIndexBar(
	uvIndex: Double,
	modifier: Modifier = Modifier,
) {
	// UV scale: 0–11+ (WHO scale)
	val clampedUv = uvIndex.coerceIn(0.0, 11.0)
	val fraction = (clampedUv / 11.0).toFloat()

	val animatedFraction by animateFloatAsState(
		targetValue = fraction,
		animationSpec = spring(stiffness = Spring.StiffnessLow),
		label = "uvBar",
	)

	val uvLevel = when {
		uvIndex < 3 -> "Low"
		uvIndex < 6 -> "Moderate"
		uvIndex < 8 -> "High"
		uvIndex < 11 -> "Very High"
		else -> "Extreme"
	}
	val uvColor = when {
		uvIndex < 3 -> Color(0xFF66BB6A)
		uvIndex < 6 -> Color(0xFFFFEE58)
		uvIndex < 8 -> Color(0xFFFFA726)
		uvIndex < 11 -> Color(0xFFEF5350)
		else -> Color(0xFFAB47BC)
	}

	Column(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(Color.White.copy(alpha = 0.12f))
			.padding(horizontal = 20.dp, vertical = 16.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "UV Index",
				style = MaterialTheme.typography.labelSmall,
				color = Color.White.copy(alpha = 0.65f),
				fontWeight = FontWeight.SemiBold,
			)
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp)
			) {
				Text(
					text = uvIndex.toInt().toString(),
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.Bold,
					color = uvColor,
					modifier = Modifier.testTag("UV Index")
				)
				Text(
					text = uvLevel,
					style = MaterialTheme.typography.labelSmall,
					color = uvColor.copy(alpha = 0.8f),
					modifier = Modifier.testTag("UvLevel"),

				)
			}
		}

		Spacer(Modifier.height(10.dp))

		// Track
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(8.dp)
				.clip(RoundedCornerShape(50))
				.background(Color.White.copy(alpha = 0.15f)),
		) {
			// Filled portion
			Box(
				modifier = Modifier
					.fillMaxWidth(animatedFraction)
					.height(8.dp)
					.clip(RoundedCornerShape(50))
					.background(
						Brush.horizontalGradient(
							listOf(
								Color(0xFF66BB6A),
								Color(0xFFFFEE58),
								Color(0xFFFFA726),
								Color(0xFFEF5350),
								Color(0xFFAB47BC),
							)
						)
					),
			)
		}

		Spacer(Modifier.height(6.dp))

		// Scale labels
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			listOf("0", "3", "6", "8", "11+").forEach { label ->
				Text(
					text = label,
					style = MaterialTheme.typography.labelSmall,
					color = Color.White.copy(alpha = 0.4f),
				)
			}
		}
	}
}

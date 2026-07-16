package durranitech.openmeteonews.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durranitech.openmeteonews.domain.model.HourlyWeather
import durranitech.openmeteonews.presentation.TemperatureUnit
import durranitech.openmeteonews.presentation.formatTemp

@Composable
fun HourlyForecastRow(
	hourly: List<HourlyWeather>,
	unit: TemperatureUnit,
	modifier: Modifier = Modifier,
) {
	LazyRow(
		modifier          = modifier.fillMaxWidth(),
		contentPadding    = PaddingValues(horizontal = 24.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		itemsIndexed(
			items = hourly,
			key   = { _, item -> item.time }, // stable key prevents layout jumps
		) { _, hour ->
			HourlyItem(
				hour = hour,
				unit = unit,
				modifier = Modifier.animateItem()
			)
		}
	}
}

@Composable
private fun HourlyItem(
	hour: HourlyWeather,
	unit: TemperatureUnit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.width(64.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(Color.White.copy(alpha = 0.12f))
			.padding(vertical = 12.dp, horizontal = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Text(
			text  = hour.time,
			style = MaterialTheme.typography.labelSmall,
			color = Color.White.copy(alpha = 0.7f),
		)
		Text(
			text     = hour.condition.emoji,
			fontSize = 20.sp,
		)
		Text(
			text       = hour.temperature.formatTemp(unit),
			style      = MaterialTheme.typography.bodySmall,
			fontWeight = FontWeight.Bold,
			color      = Color.White,
		)
		if (hour.precipitationProbability > 0) {
			Text(
				text  = "💧 ${hour.precipitationProbability}%",
				style = MaterialTheme.typography.labelSmall,
				color = Color.White.copy(alpha = 0.7f),
			)
		}
	}
}

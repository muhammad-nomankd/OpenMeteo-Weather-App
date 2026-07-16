package durranitech.openmeteonews.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import durranitech.openmeteonews.domain.model.DailyForecast
import durranitech.openmeteonews.domain.model.WeatherCondition
import durranitech.openmeteonews.presentation.TemperatureUnit
import durranitech.openmeteonews.presentation.formatTemp
import durranitech.openmeteonews.ui.theme.OpenMeteoNewsTheme

@Composable
fun OpenMeteoDailyForecastItem(
	day: DailyForecast, unit: TemperatureUnit, onClick: () -> Unit, modifier: Modifier = Modifier
) {

	Row(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(14.dp))
			.clickable(onClick = onClick)
			.padding(horizontal = 20.dp, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {

		Text(
			text = day.dayLabel,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Medium,
			color = Color.White,
			modifier = Modifier.width(80.dp),
		)

		Text(
			text = day.condition.emoji,
			fontSize = 22.sp,
		)

		Text(
			text = if (day.precipitationProbabilityMax > 0) "💧 ${day.precipitationProbabilityMax}%" else "",
			style = MaterialTheme.typography.labelSmall,
			color = Color.White.copy(alpha = 0.7f),
			modifier = Modifier.width(52.dp),
			textAlign = TextAlign.Center,
		)

		// Temp range
		Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
			Text(
				text = day.tempMax.formatTemp(unit),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold,
				color = Color.White,
			)
			Text(
				text = day.tempMin.formatTemp(unit),
				style = MaterialTheme.typography.bodyMedium,
				color = Color.White.copy(alpha = 0.5f),
			)
		}
	}

}

@Preview
@Composable
private fun OpenMeteoDailyForecastItemPreview() {
	OpenMeteoNewsTheme {
		val mockDay = DailyForecast(
			dayLabel = "Tuesday",
			tempMax = 24.5,
			tempMin = 18.2,
			condition = WeatherCondition.PartlyCloudy,
			precipitationProbabilityMax = 15,
			date = "2023-10-10",
			weatherCode = 3,
			sunrise = "07:15",
			sunset = "18:45",
			precipitationSum = 0.0,
			windSpeedMax = 12.5,
			uvIndexMax = 5.0
		)

		OpenMeteoDailyForecastItem(
			day = mockDay,
			unit = TemperatureUnit.CELSIUS,
			onClick = {},
			modifier = Modifier
				.background(MaterialTheme.colorScheme.surface)
		)
	}
}

package durranitech.openmeteonews.presentation


fun uvIndexLevel(uvIndex: Double): String = when {
	uvIndex < 3 -> "Low"
	uvIndex < 6 -> "Moderate"
	uvIndex < 8 -> "High"
	uvIndex < 11 -> "Very High"
	else -> "Extreme"
}


fun tempArcSweepAngle(
	tempMin: Double,
	tempMax: Double,
	tempCurrent: Double,
): Float {
	val range = (tempMax - tempMin).coerceAtLeast(1.0)
	return ((tempCurrent - tempMin) / range * 180.0).toFloat().coerceIn(0f, 180f)
}
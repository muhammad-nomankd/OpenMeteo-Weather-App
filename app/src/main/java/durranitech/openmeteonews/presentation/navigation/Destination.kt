package durranitech.openmeteonews.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


interface Destination : NavKey {

	@Serializable
	data object HomeDestination : Destination

	@Serializable
	data class DailyDetailDestination(
		val dailyIndex: Int, val latitude: Double, val longitude: Double
	): Destination
}
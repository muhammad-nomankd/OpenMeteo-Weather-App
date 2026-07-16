package durranitech.openmeteonews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import durranitech.openmeteonews.presentation.navigation.AppNavigation
import durranitech.openmeteonews.ui.theme.OpenMeteoNewsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			OpenMeteoNewsTheme {
				AppNavigation()
			}
		}
	}
}


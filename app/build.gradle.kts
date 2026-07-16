plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	id("com.google.devtools.ksp")
	id("com.google.dagger.hilt.android")
	alias(libs.plugins.kotlin.serialization)

}

android {
	namespace = "durranitech.openmeteonews"
	compileSdk {
		version = release(37) {
			minorApiLevel = 1
		}
	}

	defaultConfig {
		applicationId = "durranitech.openmeteonews"
		minSdk = 24
		targetSdk = 37
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			optimization {
				enable = false
			}
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.navigation3.runtime)
	implementation(libs.androidx.navigation3.ui)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.tooling)


	// Hilt
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation.compose)


	//Retrofit
	implementation(libs.retrofit.converter.kotlinx)
	implementation(libs.okhttp.logging)
	implementation(libs.retrofit)

	// Navigation
	implementation(libs.androidx.navigation.compose)

	//Kotlinx
	implementation(libs.kotlinx.serialization.json)


	// ViewModel + lifecycle-aware Compose collection
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.lifecycle.runtime.compose)

	//Material Icons
	implementation(libs.androidx.compose.material.icons.extended)

	//Splash Screen
	implementation(libs.androidx.core.splashscreen)

	//
	implementation(platform(libs.androidx.compose.bom.v20240900))






}
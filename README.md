# 🌤️ WeatherApp

A production-quality Android weather app built with **Kotlin**, **Jetpack Compose**, and **MVI Clean Architecture** — consuming the free [Open-Meteo API](https://open-meteo.com/) with no API key required.

Built as a portfolio project demonstrating Modern Android Development (MAD) best practices end-to-end: from data layer threading rules to Compose stability annotations to Navigation 3 type-safe routing.

---

## 📸 Screenshots



## ✨ Features

- 🌡️ **Current weather** — temperature, feels like, humidity, wind, pressure
- 📅 **7-day forecast** — daily high/low, condition, rain chance, UV index
- ⏱️ **24-hour hourly strip** — temperature + precipitation probability per hour
- 🌅 **Daily detail screen** — sunrise/sunset, UV bar, animated temp arc
- 🎨 **Dynamic gradient background** — changes smoothly with weather condition
- 💀 **Shimmer skeleton** — shown during initial load, not a spinner
- 🔄 **Pull-to-refresh** — Material 3 `PullToRefreshBox`, preserves existing data
- °C / °F **unit toggle** — applied across every temperature in the UI
- ✈️ **Offline error handling** — typed errors with retry, no crash on no internet

---

## 🏗️ Architecture

This project follows the official [Guide to App Architecture](https://developer.android.com/topic/architecture) with a strict three-layer separation:

```
UI Layer          →    Domain Layer    →    Data Layer
WeatherScreen          GetWeatherUseCase     WeatherRepositoryImpl
WeatherViewModel       WeatherRepository     WeatherApiService
WeatherUiState         Weather (model)       WeatherResponseDto
WeatherIntent          WeatherCondition      WeatherMapper
```

### MVI Pattern

All user actions flow through a single `onIntent(WeatherIntent)` entry point. The ViewModel processes each intent and emits a new immutable `WeatherUiState`. The UI reacts to state — it never mutates anything directly.

```
User Action → WeatherIntent → WeatherViewModel → WeatherUiState → Compose UI
                                     ↑                    |
                              GetWeatherUseCase            |
                              WeatherRepository       recomposition
                              Open-Meteo API
```

```kotlin
// Every possible UI event in one sealed class
sealed class WeatherIntent {
    data class LoadWeather(val latitude: Double, val longitude: Double) : WeatherIntent()
    data class Refresh(val latitude: Double, val longitude: Double)     : WeatherIntent()
    data class ChangeUnit(val unit: TemperatureUnit)                    : WeatherIntent()
    data object DismissError                                            : WeatherIntent()
}
```

### State Design

One state object. No scattered `isLoading`, `isError`, `isSuccess` booleans across multiple StateFlows.

```kotlin
@Immutable
data class WeatherUiState(
    val isLoading: Boolean     = false,
    val isRefreshing: Boolean  = false,   // pull-to-refresh — has data + fetching fresh
    val weather: Weather?      = null,
    val errorMessage: String?  = null,
    val unit: TemperatureUnit  = TemperatureUnit.CELSIUS,
    val locationLabel: String  = "Nowshera",
) {
    val isInitialLoading: Boolean get() = isLoading && weather == null
    val hasData: Boolean          get() = weather != null
    val isTerminalError: Boolean  get() = errorMessage != null && weather == null
}
```

---

## 🗂️ Project Structure

```
com.durranitech.weatherapp/
│
├── core/
│   └── AppResult.kt                   # sealed interface: Loading / Success / Error
│
├── data/
│   ├── remote/
│   │   ├── dto/WeatherDto.kt          # mirrors Open-Meteo JSON exactly
│   │   └── WeatherApiService.kt       # Retrofit interface
│   ├── mapper/
│   │   └── WeatherMapper.kt           # DTO → domain (pure functions, no deps)
│   └── repository/
│       └── WeatherRepositoryImpl.kt   # flowOn(IO), typed error handling
│
├── domain/
│   ├── model/
│   │   └── Weather.kt                 # @Immutable models + WMO code mapping
│   ├── repository/
│   │   └── WeatherRepository.kt       # interface (data layer implements this)
│   └── usecase/
│       └── GetWeatherUseCase.kt       # validates coords, single responsibility
│
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt           # Navigation 3, type-safe destinations
│   ├── theme/
│   │   └── Theme.kt                   # Material 3, edge-to-edge, dark
│   └── weather/
│       ├── WeatherIntent.kt           # MVI: all user/system events
│       ├── WeatherUiState.kt          # single source of truth
│       ├── WeatherViewModel.kt        # processes intents → emits state
│       ├── WeatherHomeScreen.kt       # stateless composable
│       ├── components/
│       │   ├── ShimmerEffect.kt       # InfiniteTransition shimmer skeleton
│       │   ├── HourlyForecastRow.kt   # LazyRow with stable keys
│       │   ├── DailyForecastItem.kt   # clickable daily row
│       │   └── WeatherStatCard.kt     # reusable frosted stat tile
│       └── screens/
│           └── DailyDetailScreen.kt   # Canvas arc, UV bar, sunrise/sunset
│
├── di/
│   ├── NetworkModule.kt               # Retrofit, OkHttp, Json, @Singleton
│   └── RepositoryModule.kt            # @Binds interface → impl
│
├── MainActivity.kt                    # @AndroidEntryPoint, edge-to-edge
└── WeatherApplication.kt             # @HiltAndroidApp
```

---

## 🛠️ Tech Stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin | 2.1.0 |
| UI | Jetpack Compose + Material 3 | BOM 2024.12.01 |
| Architecture | MVI + Clean Architecture | — |
| Navigation | Navigation 3 (`androidx.navigation3`) | 1.0.0-alpha02 |
| DI | Hilt | 2.52 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Serialization | Kotlinx Serialization JSON | 1.7.3 |
| Async | Coroutines + Flow + StateFlow | 1.9.0 |
| Lifecycle | `lifecycle-runtime-compose` | 2.8.7 |
| API | Open-Meteo (free, no key) | — |
| Testing | JUnit + MockK + Turbine | — |
| Min SDK | 26 (Android 8.0) | — |
| Target SDK | 35 (Android 15) | — |
| JDK | Java 21 | — |

---

## 🌐 API

Powered by [Open-Meteo](https://open-meteo.com/) — free, open-source, no API key needed for non-commercial use.

**Endpoint used:**
```
GET https://api.open-meteo.com/v1/forecast
    ?latitude=34.0151
    &longitude=71.9726
    &current=temperature_2m,relative_humidity_2m,apparent_temperature,
             is_day,precipitation,weather_code,cloud_cover,
             wind_speed_10m,wind_direction_10m,wind_gusts_10m
    &hourly=temperature_2m,precipitation_probability,weather_code,
            wind_speed_10m,relative_humidity_2m
    &daily=weather_code,temperature_2m_max,temperature_2m_min,
           sunrise,sunset,precipitation_sum,
           precipitation_probability_max,wind_speed_10m_max,uv_index_max
    &timezone=auto
    &forecast_days=7
```

**WMO weather codes** are mapped to labeled conditions, emoji icons, and gradient colors — covering all codes documented by Open-Meteo (0–99).

---

## ⚙️ Key Implementation Notes

### Threading
The repository owns dispatcher choice — not the ViewModel. `flowOn(Dispatchers.IO)` in `WeatherRepositoryImpl` shifts the Retrofit call and mapper off the main thread. The ViewModel's `viewModelScope` stays on `Main.immediate`.

```kotlin
// ✅ Correct — repository shifts to IO
override fun getForecast(...): Flow<AppResult<Weather>> = flow {
    emit(AppResult.Loading)
    emit(AppResult.Success(api.getForecast(...).toDomain()))
}.flowOn(Dispatchers.IO)

// ❌ Wrong — don't do this in the ViewModel
viewModelScope.launch(Dispatchers.IO) { ... }
```

### Compose Stability
All domain models are annotated `@Immutable`. This tells the Compose compiler they are stable, enabling the **skip optimization** — composables receiving these types are skipped during recomposition if their reference hasn't changed.

### Lifecycle-Safe Collection
```kotlin
// ✅ Stops collecting when app goes to background — saves battery
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ❌ Keeps collecting regardless of lifecycle state
val uiState by viewModel.uiState.collectAsState()
```

### LazyColumn Stable Keys
```kotlin
items(
    items = weather.daily,
    key   = { it.date },   // stable ISO date string
) { day -> ... }
```
Without a stable key, Compose falls back to positional identity and recomposes every visible row on any list change.

### Navigation 3
```kotlin
// Push to navigate forward
backStack.add(DailyDetailDestination(dayIndex = index, lat, lon))

// Pop to go back
backStack.removeLastOrNull()

// No NavController. No NavHostController. Just a list.
```

---

## 🧪 Tests

27 assertions across 4 test classes:

| Test Class | What it covers |
|---|---|
| `WeatherRepositoryTest` | Loading→Success, Loading→Error, mapper field correctness, daily item count |
| `WeatherViewModelTest` | Intent handling, state transitions using Turbine Flow test API |
| `WeatherReducerTest` | Pure reducer: all 6 state transitions without a ViewModel instance |
| `WeatherConditionTest` | Every WMO code (0–99 + unknown) maps to the correct condition |

```bash
./gradlew test
```

---

## 🚀 Getting Started

1. **Clone the repo**
   ```bash
   git https://github.com/muhammad-nomankd/OpenMeteo-Weather-App
 
   ```

2. **Open in Android Studio**
   Hedgehog (2023.1.1) or newer recommended.

3. **Run**
   No API key setup needed. Hit Run ▶️.

   Default location is **Nowshera, KP, Pakistan** (`lat 34.0151, lon 71.9726`).
   To change it, edit the coordinates in `AppNavigation.kt`:
   ```kotlin
   AppNavigation(
       viewModel = viewModel,
       startLat  = 34.0151,  // ← your latitude
       startLon  = 71.9726,  // ← your longitude
   )
   ```

---

## 🗺️ Roadmap

- [ ] GPS location via `FusedLocationProviderClient`
- [ ] City search using [Open-Meteo Geocoding API](https://geocoding-api.open-meteo.com/)
- [ ] Room DB caching — show stale data instantly, refresh in background
- [ ] Home screen widget with Glance API
- [ ] Hourly detail screen with wind direction compass
- [ ] Weather alerts / severe weather notification

---

## 📄 License

```


Copyright (c) 2025 Muhammad Noman — DurraniTech

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Author

**Muhammad Noman**
Freelance Android Developer — [DurraniTech](https://github.com/muhammad-nomankd/OpenMeteo-Weather-App)
Nowshera, Khyber Pakhtunkhwa, Pakistan

 www.linkedin.com/in/muhammad-noman-khan-durrani
[![GitHub](https://github.com/muhammad-nomankd/OpenMeteo-Weather-App)]

---

> Built with ❤️ following [developer.android.com](https://developer.android.com) — official docs first, always.
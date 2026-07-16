package durranitech.openmeteonews.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import durranitech.openmeteonews.data.remote.WeatherApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	private const val BASE_URL = "https://api.open-meteo.com/"

	@Provides
	@Singleton
	fun provideJason(): Json = Json {
		ignoreUnknownKeys = true
		coerceInputValues = true
		isLenient = true
	}


	@Provides
	@Singleton
	fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor {
		HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
		OkHttpClient.Builder().addInterceptor(logging).readTimeout(15, TimeUnit.SECONDS)
			.writeTimeout(15, TimeUnit.SECONDS).connectTimeout(15, TimeUnit.SECONDS).build()

	@Provides
	@Singleton
	fun provideRetrofit(client: OkHttpClient,json: Json): Retrofit =
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.client(client)
			.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
			.build()

	@Provides
	@Singleton
	fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
     retrofit.create<WeatherApiService>(WeatherApiService::class.java)

}
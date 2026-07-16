package durranitech.openmeteonews.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import durranitech.openmeteonews.data.repository.OpenMeteoRepositoryImp
import durranitech.openmeteonews.domain.repository.OpenMeteoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
	@Binds
	@Singleton
	abstract fun bindWeatherRepository(weatherRepository: OpenMeteoRepositoryImp): OpenMeteoRepository
}
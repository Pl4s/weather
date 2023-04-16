package com.alexplas.weathernew.di

import com.alexplas.weathernew.data.local.dao.CityDao
import com.alexplas.weathernew.data.local.dao.LocalDao
import com.alexplas.weathernew.data.mappers.CurrentWeatherMapper
import com.alexplas.weathernew.data.mappers.DailyWeatherMapper
import com.alexplas.weathernew.data.mappers.HourlyWeatherMapper
import com.alexplas.weathernew.data.mappers.SavedCityMapper
import com.alexplas.weathernew.data.repository.Repository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    fun provideMainRepository(
        cityDao: CityDao,
        localDao: LocalDao,
        currentWeatherMapper: CurrentWeatherMapper,
        dailyWeatherMapper: DailyWeatherMapper,
        hourlyWeatherMapper: HourlyWeatherMapper
    ): Repository {
        return Repository(
            cityDao,
            localDao,
            currentWeatherMapper,
            dailyWeatherMapper,
            hourlyWeatherMapper
        )
    }
}
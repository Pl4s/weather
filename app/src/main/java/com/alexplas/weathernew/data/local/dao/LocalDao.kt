package com.alexplas.weathernew.data.local.dao

import androidx.room.*
import com.alexplas.weathernew.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDao {

    // Saved City

    @Query("SELECT * FROM saved_cities WHERE isSaved = 1")
    fun getSavedCities(): Flow<List<SavedCity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSavedCity(savedCity: SavedCity)

    @Query("SELECT COUNT(*) FROM saved_cities WHERE id=:id")
    suspend fun getSavedCitiesCountById(id: Int): Int

    //delete a city from the database
    @Query("DELETE FROM saved_cities WHERE id = :cityId")
    suspend fun deleteSavedCityById(cityId: Int)

    // Current Weather

    @Query("SELECT * FROM current_weather")
    suspend fun getCurrentWeather(): CurrentWeather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)

    @Query("DELETE FROM current_weather WHERE id = :cityId")
    suspend fun deleteCurrentWeather(cityId: Int)

    // Daily Weather

    @Query("SELECT * FROM daily_weather")
    suspend fun getDailyWeather(): List<DailyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: DailyWeather)

    @Query("DELETE FROM daily_weather WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deleteDailyWeather(latitude: Double, longitude: Double)

    // Hourly Weather

    @Query("SELECT * FROM hourly_weather")
    suspend fun getHourlyWeather(): List<HourlyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: HourlyWeather)

    @Query("DELETE FROM hourly_weather WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deleteHourlyWeather(latitude: Double, longitude: Double)
}
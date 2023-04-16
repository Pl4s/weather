package com.alexplas.weathernew.data.repository


import android.util.Log
import com.alexplas.weathernew.data.api.ApiUtilities.api
import com.alexplas.weathernew.data.local.dao.CityDao
import com.alexplas.weathernew.data.local.dao.LocalDao
import com.alexplas.weathernew.data.local.entity.*
import com.alexplas.weathernew.data.mappers.CurrentWeatherMapper
import com.alexplas.weathernew.data.mappers.DailyWeatherMapper
import com.alexplas.weathernew.data.mappers.HourlyWeatherMapper
import com.alexplas.weathernew.data.model.cityweatherinfo.CityIdentifier
import com.alexplas.weathernew.data.model.cityweatherinfo.OneCallWeatherResponse
import com.alexplas.weathernew.data.model.current.CurrentWeatherResponse
import com.alexplas.weathernew.data.model.daily.DailyWeatherResponse
import com.alexplas.weathernew.data.model.hourly.HourlyWeatherResponse
import com.alexplas.weathernew.utils.Result
import com.alexplas.weathernew.utils.roundTo4DecimalPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val cityDao: CityDao,
    private val localDao: LocalDao,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val dailyWeatherMapper: DailyWeatherMapper,
    private val hourlyWeatherMapper: HourlyWeatherMapper
) {

    // Retrofit API

    suspend fun getCurrentData(
        latitude: Double,
        longitude: Double,
        unit: String
    ): Result<CurrentWeatherResponse> {
        return try {
            Log.d("WeatherRepository", "Fetching current weather data from API")
            val result = api.getCurrentWeatherResponse(latitude, longitude, unit)
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error fetching current weather data", e)
            Result.Error(e)
        }
    }

    suspend fun getHourlyDailyData(
        latitude: Double,
        longitude: Double,
        unit: String,
        exclude: String
    ): Result<OneCallWeatherResponse> {
        return try {
            Log.d("WeatherRepository", "Fetching daily weather data from API")
            val result = api.getHourlyDailyWeatherResponse(latitude, longitude, unit, exclude)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // City

    fun getCitiesByName(query: String): Flow<List<City>> {
        return cityDao.getCitiesByName(query)
    }

    // Local

    // Get all saved cities from the database and map them to SavedCity objects
    fun getSavedCities(): Flow<List<SavedCity>> {
        Log.d("GetSavedCitiesRepo", "Fetching saved cities from the database")
        return localDao.getSavedCities()
    }

    suspend fun isCityAlreadySaved(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            localDao.getSavedCitiesCountById(id) == 1
        }
    }

    // Insert a city into the database
    suspend fun insertSavedCity(savedCity: SavedCity) {
        try {
            val cityToInsert = savedCity.copy(isSaved = 1) // Set isSaved to 1 when inserting the city
            withContext(Dispatchers.IO) {
                localDao.insertSavedCity(cityToInsert)

                // Update the isSaved value in the cities table
                cityDao.updateCityIsSaved(savedCity.id, 1)
            }
            Log.d("InsertCityRepo", "Inserted city: $cityToInsert")
        } catch (e: Exception) {
            Log.e("InsertCityRepo", "Failed to insert city: $savedCity", e)
        }
    }

    suspend fun getCurrentWeatherData(): Result<CurrentWeather> {
        return try {
            val currentWeather = withContext(Dispatchers.IO) {
                localDao.getCurrentWeather()
            }
            if (currentWeather != null) {
                Result.Success(currentWeather)
            } else {
                Result.Error(NullPointerException("Current weather data not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getDailyHourlyWeatherData(): Result<Pair<List<DailyWeather>, List<HourlyWeather>>> {
        return try {
            val dailyWeather = withContext(Dispatchers.IO) {
                localDao.getDailyWeather()
            }
            val hourlyWeather = withContext(Dispatchers.IO) {
                localDao.getHourlyWeather()
            }
            Result.Success(Pair(dailyWeather, hourlyWeather))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun cacheCurrentWeather(currentWeatherResponse: CurrentWeatherResponse): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                localDao.insertCurrentWeather(
                    currentWeatherMapper.mapToCurrentWeatherEntity(currentWeatherResponse)
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun cacheHourlyDailyWeather(
        latitude: Double,
        longitude: Double,
        dailyWeatherResponses: List<DailyWeatherResponse>,
        hourlyWeatherResponses: List<HourlyWeatherResponse>
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val roundedLatitude = roundTo4DecimalPlaces(latitude)
                val roundedLongitude = roundTo4DecimalPlaces(longitude)

                dailyWeatherResponses.forEach { dailyWeatherResponse ->
                    val dailyWeatherEntity = dailyWeatherMapper.mapToDailyWeatherEntity(
                        dailyWeatherResponse, roundedLatitude, roundedLongitude
                    )
                    Log.d(
                        "CacheWeatherRepo",
                        "Caching daily weather for lat: $roundedLatitude, lon: $roundedLongitude"
                    )
                    localDao.insertDailyWeather(dailyWeatherEntity)
                }
                hourlyWeatherResponses.forEach { hourlyWeatherResponse ->
                    val hourlyWeatherEntity = hourlyWeatherMapper.mapToHourlyWeatherEntity(
                        hourlyWeatherResponse,
                        roundedLatitude,
                        roundedLongitude
                    )
                    Log.d(
                        "CacheWeatherRepo",
                        "Caching hourly weather for lat: $roundedLatitude, lon: $roundedLongitude"
                    )
                    localDao.insertHourlyWeather(hourlyWeatherEntity)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun deleteCityWeather(cityIdentifier: CityIdentifier): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                Log.d(
                    "DeleteCityRepo",
                    "Deleting current weather for city ID: ${cityIdentifier.id}"
                ) // Add this line
                localDao.deleteCurrentWeather(cityIdentifier.id)
                Log.d(
                    "DeleteCityRepo",
                    "Deleting daily weather for lat: ${cityIdentifier.lat}, lon: ${cityIdentifier.lon}"
                ) // Add this line
                localDao.deleteDailyWeather(cityIdentifier.lat, cityIdentifier.lon)
                Log.d(
                    "DeleteCityRepo",
                    "Deleting hourly weather for lat: ${cityIdentifier.lat}, lon: ${cityIdentifier.lon}"
                )
                localDao.deleteHourlyWeather(cityIdentifier.lat, cityIdentifier.lon)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun deleteCity(cityIdentifier: CityIdentifier): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                localDao.deleteSavedCityById(cityIdentifier.id)
                cityDao.updateCityIsSaved(cityIdentifier.id, 0)
                Log.d("DeleteCityRepo", "Deleted city: $cityIdentifier")
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}
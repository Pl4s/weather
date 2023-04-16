package com.alexplas.weathernew.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.alexplas.weathernew.data.local.entity.*
import com.alexplas.weathernew.data.mappers.CurrentWeatherMapper
import com.alexplas.weathernew.data.mappers.DailyWeatherMapper
import com.alexplas.weathernew.data.mappers.HourlyWeatherMapper
import com.alexplas.weathernew.data.mappers.SavedCityMapper
import com.alexplas.weathernew.data.model.cityweatherinfo.CityIdentifier
import com.alexplas.weathernew.data.model.cityweatherinfo.OneCallWeatherResponse
import com.alexplas.weathernew.data.model.current.CurrentWeatherResponse
import com.alexplas.weathernew.data.model.daily.DailyWeatherResponse
import com.alexplas.weathernew.data.model.hourly.HourlyWeatherResponse
import com.alexplas.weathernew.data.repository.Repository
import com.alexplas.weathernew.utils.Event
import com.alexplas.weathernew.utils.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.alexplas.weathernew.utils.Result
import com.alexplas.weathernew.utils.asLiveData
import kotlinx.coroutines.flow.first

@HiltViewModel
class MyViewModel @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val repository: Repository,
    private val savedCityMapper: SavedCityMapper,
    private val currentWeatherMapper: CurrentWeatherMapper,
    private val dailyWeatherMapper: DailyWeatherMapper,
    private val hourlyWeatherMapper: HourlyWeatherMapper
) : ViewModel() {

    private val _current = MutableLiveData<CurrentWeatherResponse?>()
    val current = _current.asLiveData()

    private val _addCities = MutableLiveData<Unit?>()
    val addCities = _addCities.asLiveData()

    private val _refreshCities = MutableLiveData<Unit?>()
    val refreshCities = _refreshCities.asLiveData()

    private val _deleteCities = MutableLiveData<Unit?>()
    val deleteCities = _deleteCities.asLiveData()

    private val _hourlyDaily = MutableLiveData<OneCallWeatherResponse>()
    val hourlyDaily = _hourlyDaily.asLiveData()

    private val _savedCities = MutableLiveData<List<SavedCity>>(emptyList())
    val savedCities = _savedCities.asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _error = MutableLiveData<String>()
    val error = _error.asLiveData()

    private val _showSnackbar = MutableLiveData<Event<String>>()
    val showSnackbar: LiveData<Event<String>> = _showSnackbar

    val weatherUnit: LiveData<String> = preferencesDataStore.weatherUnitFlow.asLiveData()

    init {
        getSavedCities()
    }

    // Current Weather Data
    fun getCurrentData(latitude: Double, longitude: Double, unit: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val weatherUnit = preferencesDataStore.weatherUnitFlow.first()
            Log.d("MyViewModel", "Fetching current weather data")
            when (val result = repository.getCurrentData(latitude, longitude, weatherUnit)) {
                is Result.Success -> {
                    cacheCurrentData(result.data) // Cache the fetched data
                    _isLoading.postValue(false)
                    _current.postValue(result.data)
                }
                is Result.Error -> {
                    getLocalCurrentData() // Fetch data from the local database if there's an error
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                    Log.e("MyViewModel", "Error fetching current weather data", result.exception)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }


    // Hourly and Daily Weather Data

    fun getHourlyDailyWeatherData(latitude: Double, longitude: Double, exclude: String, unit: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val weatherUnit = preferencesDataStore.weatherUnitFlow.first()
            when (val result = repository.getHourlyDailyData(latitude, longitude, weatherUnit,exclude)) {
                is Result.Success -> {
                    cacheHourlyDailyData(latitude, longitude, result.data.daily, result.data.hourly) // Cache the fetched data
                    _isLoading.postValue(false)
                    _hourlyDaily.postValue(result.data)
                }
                is Result.Error -> {
                    getLocalHourlyDailyData() // Fetch data from the local database if there's an error
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }

    // Search for a city by name

    fun getCities(query: String): LiveData<List<City>> {
        return repository.getCitiesByName(query).asLiveData()
    }

    // Fetches a list of saved cities from the repository, updates the UI state, and handles errors and loading states.

    fun getSavedCities() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val savedCities = repository.getSavedCities().first()
                val updatedCities = savedCities.map { city ->
                    val weatherUnit = preferencesDataStore.weatherUnitFlow.first()
                    val currentWeatherResult = repository.getCurrentData(city.lat, city.lon, weatherUnit)
                    if (currentWeatherResult is Result.Success) {
                        val currentWeather = currentWeatherResult.data
                        savedCityMapper.mapToSavedCityEntity(currentWeather).apply {
                            isSaved = city.isSaved
                        }
                    } else {
                        city
                    }
                }
                Log.d("ViewModel", "Emitting saved cities: $updatedCities")
                _savedCities.postValue(updatedCities)
                Log.d("GetSavedCities", "Updated _savedCities LiveData: $updatedCities")
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _error.postValue(e.message)
                _isLoading.postValue(false)
            }
        }
    }

    fun refreshWeather(latitude: Double, longitude: Double, exclude: String = "minutely,alerts", unit: String) {
        _isLoading.value = true

        viewModelScope.launch {
            getCurrentData(latitude, longitude, unit)
        }

        viewModelScope.launch {
            getHourlyDailyWeatherData(latitude, longitude, exclude, unit)
        }
    }

    fun insertCity(city: City) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val weatherUnit = preferencesDataStore.weatherUnitFlow.first()
                when (val currentWeatherResult = repository.getCurrentData(city.lon, city.lat, weatherUnit)) {
                    is Result.Success -> {
                        val currentWeather = currentWeatherResult.data
                        val savedCity = savedCityMapper.mapToSavedCityEntity(currentWeather)
                        if (!repository.isCityAlreadySaved(savedCity.id)) {
                            repository.insertSavedCity(savedCity)
                        }
                        getSavedCities()
                        Log.d("InsertCity", "getSavedCities() called after city insertion")
                        _showSnackbar.postValue(Event("City added"))
                        _isLoading.postValue(false)
                    }
                    is Result.Error -> {
                        _isLoading.postValue(false)
                        _error.postValue(currentWeatherResult.exception.message)
                    }
                    is Result.Loading -> _isLoading.postValue(true)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _error.postValue(e.message)
            }
        }
    }

    // Delete City from RecyclerView

    fun deleteCity(cityIdentifier: CityIdentifier) {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("DeleteCity", "City ID: ${cityIdentifier.id}, Latitude: ${cityIdentifier.lat}, Longitude: ${cityIdentifier.lon}")
            when (val result = repository.deleteCity(cityIdentifier)) {
                is Result.Success -> {
                    // Call the deleteCityWeather function with the cityId, latitude, and longitude
                    when (val deleteWeatherResult = repository.deleteCityWeather(cityIdentifier)) {
                        is Result.Success -> {
                            _deleteCities.postValue(null)
                            getSavedCities()
                            _isLoading.postValue(false)
                        }
                        is Result.Error -> {
                            _isLoading.postValue(false)
                            _error.postValue(deleteWeatherResult.exception.message)
                        }
                        is Result.Loading -> _isLoading.postValue(true)
                    }
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }

    // Restore City
    fun restoreCity(cityIdentifier: CityIdentifier) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val weatherUnit = preferencesDataStore.weatherUnitFlow.first()
                when (val currentWeatherResult =
                    repository.getCurrentData(cityIdentifier.lat, cityIdentifier.lon, weatherUnit)) {
                    is Result.Success -> {
                        val currentWeather = currentWeatherResult.data
                        val savedCity = savedCityMapper.mapToSavedCityEntity(currentWeather)
                        if (!repository.isCityAlreadySaved(savedCity.id)) {
                            repository.insertSavedCity(savedCity)
                            getSavedCities()
                        }
                        getSavedCities()
                        Log.d("InsertCity", "getSavedCities() called after city insertion")
                        _showSnackbar.postValue(Event("City restored"))
                        _isLoading.postValue(false)
                    }
                    is Result.Error -> {
                        _isLoading.postValue(false)
                        _error.postValue(currentWeatherResult.exception.message)
                    }
                    is Result.Loading -> _isLoading.postValue(true)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
                _error.postValue(e.message)
            }
        }
    }

    private fun cacheCurrentData(currentWeatherResponse: CurrentWeatherResponse) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.cacheCurrentWeather(currentWeatherResponse)) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }

    private fun cacheHourlyDailyData(
        latitude: Double,
        longitude: Double,
        dailyWeatherResponses: List<DailyWeatherResponse>,
        hourlyWeatherResponses: List<HourlyWeatherResponse>
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result =
                repository.cacheHourlyDailyWeather(latitude,longitude, dailyWeatherResponses, hourlyWeatherResponses)) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }

    fun getLocalCurrentData() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getCurrentWeatherData()) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                    val currentWeatherResponse =
                        currentWeatherMapper.mapToCurrentWeatherResponse(result.data)
                    _current.postValue(currentWeatherResponse)
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }

    fun getLocalHourlyDailyData() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getDailyHourlyWeatherData()) {
                is Result.Success -> {
                    _isLoading.postValue(false)
                    val dailyWeatherResponseList =
                        result.data.first.map { dailyWeather ->
                            dailyWeatherMapper.mapToDailyWeatherResponse(dailyWeather)
                        }
                    val hourlyWeatherResponseList =
                        result.data.second.map { hourlyWeather ->
                            hourlyWeatherMapper.mapToHourlyWeatherResponse(hourlyWeather)
                        }
                    val latitude = result.data.first.firstOrNull()?.latitude ?: 0.0
                    val longitude = result.data.first.firstOrNull()?.longitude ?: 0.0
                    _hourlyDaily.postValue(
                        OneCallWeatherResponse(
                            latitude = latitude,
                            longitude = longitude,
                            daily = dailyWeatherResponseList,
                            hourly = hourlyWeatherResponseList
                        )
                    )
                }
                is Result.Error -> {
                    _isLoading.postValue(false)
                    _error.postValue(result.exception.message)
                }
                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }
}




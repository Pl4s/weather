package com.alexplas.weathernew.data.mappers

import com.alexplas.weathernew.data.local.entity.SavedCity
import com.alexplas.weathernew.data.model.current.CurrentWeatherResponse
import com.alexplas.weathernew.utils.unixTimestampToLocalTimeString
import com.alexplas.weathernew.utils.unixTimestampToTimeString
import javax.inject.Inject
import kotlin.math.roundToInt

class SavedCityMapper
@Inject constructor() {
    fun mapToSavedCityEntity(response: CurrentWeatherResponse): SavedCity {
        return SavedCity(
            id = response.id,
            name = response.name,
            country = response.sys.country,
            lat = response.coord.lat,
            lon = response.coord.lon,
            localTime = response.dt.unixTimestampToLocalTimeString(response.timezone),
            currentTemp = response.main.temp.roundToInt(),
            weatherIcon = response.weather[0].icon,
            minTemp = response.main.tempMin.roundToInt(),
            maxTemp = response.main.tempMax.roundToInt(),
            weatherId = response.weather[0].id,
            isSaved = 1
        )
    }
}
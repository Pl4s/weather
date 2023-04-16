package com.alexplas.weathernew.data.mappers

import com.alexplas.weathernew.data.local.entity.HourlyWeather
import com.alexplas.weathernew.data.model.hourly.HourlyWeatherResponse
import com.alexplas.weathernew.data.model.hourly.Weather
import com.alexplas.weathernew.utils.timeStringToUnixTimestamp
import com.alexplas.weathernew.utils.unixTimestampToTimeString
import javax.inject.Inject
import kotlin.math.roundToInt

class HourlyWeatherMapper
@Inject constructor() {

    fun mapToHourlyWeatherEntity(response: HourlyWeatherResponse, latitude: Double, longitude: Double): HourlyWeather {
        return HourlyWeather(
            latitude = latitude,
            longitude = longitude,
            time = response.dt.unixTimestampToTimeString(),
            weatherIcon = response.weather[0].icon,
            currentTemp = response.temp.roundToInt()
        )
    }

    fun mapToHourlyWeatherResponse(hourlyWeather: HourlyWeather): HourlyWeatherResponse {
        return HourlyWeatherResponse(
            dt = hourlyWeather.time.timeStringToUnixTimestamp(),
            temp = hourlyWeather.currentTemp.toDouble(),
            weather = listOf(Weather(icon = hourlyWeather.weatherIcon))
        )
    }
}
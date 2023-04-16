package com.alexplas.weathernew.data.mappers

import com.alexplas.weathernew.data.local.entity.DailyWeather
import com.alexplas.weathernew.data.model.daily.DailyWeatherResponse
import com.alexplas.weathernew.data.model.daily.Temp
import com.alexplas.weathernew.data.model.daily.Weather
import com.alexplas.weathernew.utils.dateStringToUnixTimestamp
import com.alexplas.weathernew.utils.unixTimestampToDateString
import javax.inject.Inject
import kotlin.math.roundToInt

class DailyWeatherMapper
@Inject constructor() {

    fun mapToDailyWeatherEntity(response: DailyWeatherResponse, latitude: Double, longitude: Double): DailyWeather {
        return DailyWeather(
            latitude= latitude,
            longitude = longitude,
            date = response.dt.unixTimestampToDateString(),
            tempDay = response.temp.day.roundToInt(),
            weatherIcon = response.weather[0].icon,
            minTemp = response.temp.min.roundToInt(),
            maxTemp = response.temp.max.roundToInt()
        )
    }

    fun mapToDailyWeatherResponse(dailyWeather: DailyWeather): DailyWeatherResponse {
        return DailyWeatherResponse(
            dt = dailyWeather.date.dateStringToUnixTimestamp(),
            temp = Temp(
                day = dailyWeather.tempDay.toDouble(),
                min = dailyWeather.minTemp.toDouble(),
                max = dailyWeather.maxTemp.toDouble()
            ),
            weather = listOf(
                Weather(
                    icon = dailyWeather.weatherIcon
                )
            )
        )
    }
}
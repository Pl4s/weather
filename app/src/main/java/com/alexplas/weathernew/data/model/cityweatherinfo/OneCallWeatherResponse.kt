package com.alexplas.weathernew.data.model.cityweatherinfo

import com.alexplas.weathernew.data.model.daily.DailyWeatherResponse
import com.alexplas.weathernew.data.model.hourly.HourlyWeatherResponse

data class OneCallWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val daily: List<DailyWeatherResponse>,
    val hourly: List<HourlyWeatherResponse>
)
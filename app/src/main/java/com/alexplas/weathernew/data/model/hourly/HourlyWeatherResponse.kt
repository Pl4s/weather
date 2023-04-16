package com.alexplas.weathernew.data.model.hourly

import com.google.gson.annotations.SerializedName

data class HourlyWeatherResponse(


    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: Double,
    @SerializedName("weather") val weather: List<Weather>
)
package com.alexplas.weathernew.data.model.daily

import com.google.gson.annotations.SerializedName

data class DailyWeatherResponse(


    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: Temp,
    @SerializedName("weather") val weather: List<Weather>,
)

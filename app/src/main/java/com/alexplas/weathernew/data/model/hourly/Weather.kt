package com.alexplas.weathernew.data.model.hourly

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("icon") val icon: String
)

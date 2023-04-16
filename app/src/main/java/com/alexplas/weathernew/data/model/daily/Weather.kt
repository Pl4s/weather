package com.alexplas.weathernew.data.model.daily

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("icon") val icon: String
)

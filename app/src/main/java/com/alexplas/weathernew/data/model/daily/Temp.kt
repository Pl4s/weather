package com.alexplas.weathernew.data.model.daily

import com.google.gson.annotations.SerializedName

data class Temp (

    @SerializedName("day") val day: Double,
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double
)
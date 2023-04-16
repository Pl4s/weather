package com.alexplas.weathernew.data.model.current

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

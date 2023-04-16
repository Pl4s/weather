package com.alexplas.weathernew.data.model.current

import com.google.gson.annotations.SerializedName

data class Sys(

    @SerializedName ("id") val id: Int,
    @SerializedName ("country") val country: String,
    @SerializedName ("sunrise") val sunrise: Long,
    @SerializedName ("sunset") val sunset: Long
    )

package com.alexplas.weathernew.data.model.daily

import com.google.gson.annotations.SerializedName

data class FeelsLike(

    @SerializedName("day") val day: Int,
    @SerializedName("night") val night: Int,
    @SerializedName("eve") val eve: Int,
    @SerializedName("morn") val morn: Int
)

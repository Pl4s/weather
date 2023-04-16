package com.alexplas.weathernew.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "hourly_weather", primaryKeys = ["latitude", "longitude"])
data class HourlyWeather(

    @ColumnInfo ("latitude") val latitude: Double,
    @ColumnInfo ("longitude") val longitude: Double,
    @ColumnInfo ("time") var time: String,
    @ColumnInfo ("weather_icon") val weatherIcon: String,
    @ColumnInfo ("current_temp") val currentTemp: Int
)

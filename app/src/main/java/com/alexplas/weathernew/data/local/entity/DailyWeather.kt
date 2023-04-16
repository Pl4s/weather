package com.alexplas.weathernew.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "daily_weather", primaryKeys = ["latitude", "longitude"])
data class DailyWeather(

    @ColumnInfo (name = "latitude") val latitude: Double,
    @ColumnInfo (name = "longitude") val longitude: Double,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "temp_day") var tempDay: Int,
    @ColumnInfo(name = "weather_icon") var weatherIcon: String,
    @ColumnInfo(name = "min_temp") var minTemp: Int,
    @ColumnInfo(name = "max_temp") var maxTemp: Int,
)
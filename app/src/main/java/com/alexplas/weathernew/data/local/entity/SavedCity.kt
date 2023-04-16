package com.alexplas.weathernew.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "latitude") val lat: Double,
    @ColumnInfo(name = "longitude") val lon: Double,
    @ColumnInfo(name = "localTime") var localTime: String,
    @ColumnInfo(name = "current_temp") var currentTemp: Int,
    @ColumnInfo(name = "weather_icon") var weatherIcon: String,
    @ColumnInfo(name = "min_temp") var minTemp: Int,
    @ColumnInfo(name = "max_temp") var maxTemp: Int,
    @ColumnInfo(name = "weather_id") var weatherId: Int,
    @ColumnInfo(name = "isSaved") var isSaved: Int
)
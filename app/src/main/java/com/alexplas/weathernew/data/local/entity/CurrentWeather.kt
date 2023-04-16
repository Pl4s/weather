package com.alexplas.weathernew.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "current_temp") var currentTemp: Int,
    @ColumnInfo(name = "weather_description") var weatherDescription: String,
    @ColumnInfo(name = "weather_id") var weatherID: Int,
    @ColumnInfo(name = "min_temp") var minTemp: Int,
    @ColumnInfo(name = "max_temp") var maxTemp: Int,
    @ColumnInfo(name = "sunrise") var sunrise: Long,
    @ColumnInfo(name = "sunset") var sunset: Long,
    @ColumnInfo(name = "feels_like") val feelsLike: Int,
    @ColumnInfo(name = "humidity") val humidity: Int,
    @ColumnInfo(name = "visibility") val visibility: Int,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "pressure") val pressure: Int,
    @ColumnInfo(name = "clouds") val clouds: Int
)

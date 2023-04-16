package com.alexplas.weathernew.data.api

import androidx.lifecycle.LiveData
import com.alexplas.weathernew.data.model.cityweatherinfo.OneCallWeatherResponse
import com.alexplas.weathernew.data.model.current.CurrentWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("2.5/weather?")
    suspend fun getCurrentWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String
    ):CurrentWeatherResponse

    @GET("3.0/onecall?")
    suspend fun getHourlyDailyWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") unit: String,
        @Query("exclude") exclude: String
    ): OneCallWeatherResponse
}
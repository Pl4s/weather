package com.alexplas.weathernew.data.mappers

import com.alexplas.weathernew.data.local.entity.CurrentWeather
import com.alexplas.weathernew.data.model.current.*
import javax.inject.Inject
import kotlin.math.roundToInt

class CurrentWeatherMapper
@Inject constructor() {
    fun mapToCurrentWeatherEntity(response: CurrentWeatherResponse): CurrentWeather {
        return CurrentWeather(
            id = response.id,
            name = response.name,
            country = response.sys.country,
            currentTemp = response.main.temp.roundToInt(),
            weatherDescription = response.weather[0].description,
            weatherID = response.weather[0].id,
            minTemp = response.main.tempMin.roundToInt(),
            maxTemp = response.main.tempMax.roundToInt(),
            sunrise = response.sys.sunrise,
            sunset = response.sys.sunset,
            feelsLike = response.main.feelsLike.roundToInt(),
            humidity = response.main.humidity,
            visibility = response.visibility,
            windSpeed = response.wind.speed,
            pressure = response.main.pressure,
            clouds = response.clouds.all
        )
    }

    fun mapToCurrentWeatherResponse(currentWeather: CurrentWeather): CurrentWeatherResponse {
        return CurrentWeatherResponse(
            name = currentWeather.name,
            sys = Sys(
                0,
                country = currentWeather.country,
                sunrise = currentWeather.sunrise,
                sunset = currentWeather.sunset
            ),
            main = Main(
                temp = currentWeather.currentTemp.toDouble(),
                tempMin = currentWeather.minTemp.toDouble(),
                tempMax = currentWeather.maxTemp.toDouble(),
                feelsLike = currentWeather.feelsLike.toDouble(),
                humidity = currentWeather.humidity,
                pressure = currentWeather.pressure
            ),
            weather = listOf(
                Weather(
                    id = currentWeather.weatherID,
                    description = currentWeather.weatherDescription,
                    ""
                )
            ),
            visibility = currentWeather.visibility,
            wind = Wind(speed = currentWeather.windSpeed, deg = 0),
            clouds = Clouds(all = currentWeather.clouds),
            dt = 0,
            coord = Coord(0.0, 0.0),
            id = 0,
            timezone = 0
        )

    }


}
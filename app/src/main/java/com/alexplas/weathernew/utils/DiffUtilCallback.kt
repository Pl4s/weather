package com.alexplas.weathernew.utils

import androidx.recyclerview.widget.DiffUtil
import com.alexplas.weathernew.data.local.entity.City
import com.alexplas.weathernew.data.local.entity.DailyWeather
import com.alexplas.weathernew.data.local.entity.HourlyWeather
import com.alexplas.weathernew.data.local.entity.SavedCity

class DiffUtilCallbackSavedCity: DiffUtil.ItemCallback<SavedCity>() {
    override fun areItemsTheSame(
        oldItem: SavedCity,
        newItem: SavedCity
    ): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SavedCity,
        newItem: SavedCity
    ): Boolean {
        return oldItem==newItem
    }
}

class DiffUtilCallbackSearchCity: DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(
        oldItem: City,
        newItem: City
    ): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(
        oldItem: City,
        newItem: City
    ): Boolean {
        return oldItem==newItem
    }
}

class DiffUtilCallbackDaily: DiffUtil.ItemCallback<DailyWeather>(){
    override fun areItemsTheSame(
        oldItem: DailyWeather,
        newItem: DailyWeather
    ): Boolean {
        return oldItem.date==newItem.date
    }

    override fun areContentsTheSame(
        oldItem: DailyWeather,
        newItem: DailyWeather
    ): Boolean {
        return oldItem==newItem
    }
}

class DiffUtilCallbackHourly: DiffUtil.ItemCallback<HourlyWeather>(){
    override fun areItemsTheSame(
        oldItem: HourlyWeather,
        newItem: HourlyWeather
    ): Boolean {
        return oldItem.time==newItem.time
    }

    override fun areContentsTheSame(
        oldItem: HourlyWeather,
        newItem: HourlyWeather
    ): Boolean {
        return oldItem==newItem
    }
}
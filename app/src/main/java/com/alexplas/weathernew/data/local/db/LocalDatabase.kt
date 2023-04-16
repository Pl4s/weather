package com.alexplas.weathernew.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexplas.weathernew.data.local.dao.LocalDao
import com.alexplas.weathernew.data.local.entity.*

@Database(
    entities = [
        CurrentWeather::class,
        DailyWeather::class,
        HourlyWeather::class,
        SavedCity::class],
    version = 2, exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun localDao(): LocalDao

    companion object {
        const val DATABASE_NAME = "local_db"
    }
}
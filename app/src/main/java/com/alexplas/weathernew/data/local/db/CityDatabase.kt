package com.alexplas.weathernew.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexplas.weathernew.data.local.dao.CityDao
import com.alexplas.weathernew.data.local.entity.City

@Database(
    entities = [
        City::class
    ], version = 3, exportSchema = false
)
abstract class CityDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {
        const val DATABASE_NAME = "cities_db"
    }
}
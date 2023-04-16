package com.alexplas.weathernew.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.alexplas.weathernew.data.local.entity.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM city WHERE name LIKE :query || '%'")
    fun getCitiesByName(query: String): Flow<List<City>>

    @Query("UPDATE city SET isSaved = :isSaved WHERE id = :cityId")
    suspend fun updateCityIsSaved(cityId: Int, isSaved: Int)
}
package com.alexplas.weathernew.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesDataStore(private val context: Context) {
    private val dataStore = context.dataStore

    val weatherUnitFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[WEATHER_UNIT_KEY] ?: getDefaultWeatherUnit()
    }

    suspend fun saveWeatherUnit(unit: String) {
        dataStore.edit { preferences ->
            preferences[WEATHER_UNIT_KEY] = unit
        }
    }

    private fun getDefaultWeatherUnit(): String {
        return "metric"
    }

    companion object {
        private val WEATHER_UNIT_KEY = stringPreferencesKey("weather_unit")
    }
}
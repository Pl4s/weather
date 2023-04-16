package com.alexplas.weathernew.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.alexplas.weathernew.R
import com.alexplas.weathernew.utils.PreferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Settings : PreferenceFragmentCompat() {

    private lateinit var preferencesDataStore: PreferencesDataStore
    private lateinit var weatherUnitPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        preferencesDataStore = PreferencesDataStore(requireContext())
        weatherUnitPreference = findPreference(getString(R.string.unit_key))!!

        // Collect the weather unit changes
        weatherUnitPreference.setOnPreferenceChangeListener { _, newValue ->
            lifecycleScope.launch {
                preferencesDataStore.saveWeatherUnit(newValue as String)
                updateWeatherUnitPreference(newValue)
            }
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.context?.theme?.applyStyle(R.style.SettingsFragmentStyle, true)
        return view
    }

    private fun updateWeatherUnitPreference(newWeatherUnit: String) {
        weatherUnitPreference.value = newWeatherUnit
    }
}

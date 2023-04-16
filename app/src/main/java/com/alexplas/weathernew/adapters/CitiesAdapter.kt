package com.alexplas.weathernew.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexplas.weathernew.R
import com.alexplas.weathernew.data.local.entity.SavedCity
import com.alexplas.weathernew.databinding.CitiesListBinding
import com.alexplas.weathernew.utils.DiffUtilCallbackSavedCity
import com.alexplas.weathernew.utils.getBackgroundResource
import com.alexplas.weathernew.utils.loadWeatherIcon

class CitiesAdapter : ListAdapter<SavedCity, CitiesAdapter.CitiesViewHolder>(DiffUtilCallbackSavedCity()) {

    private var onItemClickListener: ((SavedCity) -> Unit)? = null

    fun setOnItemClickListener(listener: (SavedCity) -> Unit) {
        onItemClickListener = listener
    }

    class CitiesViewHolder(val binding: CitiesListBinding) : RecyclerView.ViewHolder(binding.root) {
        val foregroundView: View = binding.cvForeground

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CitiesListBinding.inflate(inflater, parent, false)
        return CitiesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        val cities = getItem(position)
        bind(holder.binding, cities)
    }

    @SuppressLint("SetTextI18n")
    private fun bind(binding: CitiesListBinding, cities: SavedCity?) {
        binding.apply {

            (binding.cvForeground.getChildAt(0) as FrameLayout).setBackgroundResource(getBackgroundResource(cities?.weatherId ?: 0))
            tvCityCitiesList.text = cities?.name
            tvTimeCitiesList.text = cities?.localTime
            loadWeatherIcon(ivIconCityList.context, cities?.weatherIcon ?: "", ivIconCityList)
            tvTempDailyList.text = binding.root.context.getString(R.string.celsius_symbol, cities?.currentTemp)
            tvTempMinDailyList.text = binding.root.context.getString(R.string.celsius_symbol, cities?.minTemp)
            tvTempMaxDailyList.text = binding.root.context.getString(R.string.celsius_symbol, cities?.maxTemp)

            binding.root.setOnClickListener {
                onItemClickListener?.let { it(cities!!) }
            }
        }
    }

    fun getCityAtPosition(position: Int): SavedCity {
        return getItem(position)
    }
}
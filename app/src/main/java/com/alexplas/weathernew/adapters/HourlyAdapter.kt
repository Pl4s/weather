package com.alexplas.weathernew.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexplas.weathernew.R
import com.alexplas.weathernew.data.local.entity.HourlyWeather
import com.alexplas.weathernew.databinding.HourlyListBinding
import com.alexplas.weathernew.utils.DiffUtilCallbackHourly
import com.alexplas.weathernew.utils.loadWeatherIcon

class HourlyAdapter : ListAdapter<HourlyWeather, HourlyAdapter.HourlyViewHolder>(DiffUtilCallbackHourly()) {

    class HourlyViewHolder(val binding: HourlyListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HourlyListBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourly = getItem(position)
        bind(holder.binding, hourly)
    }

    private fun bind(binding: HourlyListBinding, hourly: HourlyWeather?) {
        binding.apply {
            tvTimeHourlyList.text = hourly?.time
            loadWeatherIcon(
                ivIconHourlyList.context,
                hourly?.weatherIcon ?: "",
                ivIconHourlyList
            )
            tvTemperatureHourlyList.text = binding.root.context.getString(R.string.celsius_symbol, hourly?.currentTemp)
        }
    }
}

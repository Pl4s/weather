package com.alexplas.weathernew.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexplas.weathernew.R
import com.alexplas.weathernew.data.local.entity.DailyWeather
import com.alexplas.weathernew.databinding.DailyListBinding
import com.alexplas.weathernew.utils.DiffUtilCallbackDaily
import com.alexplas.weathernew.utils.loadWeatherIcon

class DailyAdapter : ListAdapter<DailyWeather, DailyAdapter.DailyViewHolder>(DiffUtilCallbackDaily()) {

    class DailyViewHolder(val binding: DailyListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DailyListBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val daily = getItem(position)
        bind(holder.binding, daily)
    }

    private fun bind(binding: DailyListBinding, daily: DailyWeather?) {
        binding.apply {
            tvDateDailyList.text = daily?.date
            loadWeatherIcon(ivIconDailyList.context, daily?.weatherIcon ?: "", ivIconDailyList)
            tvTemperatureDailyList.text = binding.root.context.getString(R.string.celsius_symbol, daily?.tempDay)
            tvTempMinDailyList.text = binding.root.context.getString(R.string.celsius_symbol,daily?.minTemp)
            tvTempMaxDailyList.text = binding.root.context.getString(R.string.celsius_symbol, daily?.maxTemp)
        }
    }
}
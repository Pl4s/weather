package com.alexplas.weathernew.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexplas.weathernew.R
import com.alexplas.weathernew.adapters.DailyAdapter
import com.alexplas.weathernew.adapters.HourlyAdapter
import com.alexplas.weathernew.data.mappers.DailyWeatherMapper
import com.alexplas.weathernew.data.mappers.HourlyWeatherMapper
import com.alexplas.weathernew.databinding.FragmentCityDetailsBinding
import com.alexplas.weathernew.utils.PreferencesDataStore
import com.alexplas.weathernew.utils.WindDeg
import com.alexplas.weathernew.utils.getBackgroundResource
import com.alexplas.weathernew.utils.unixTimestampToTimeString
import com.alexplas.weathernew.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class CityDetails : Fragment() {

    private var _binding: FragmentCityDetailsBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MyViewModel by activityViewModels()

    @Inject
    lateinit var hourlyWeatherMapper: HourlyWeatherMapper

    @Inject
    lateinit var dailyWeatherMapper: DailyWeatherMapper

    @Inject
    lateinit var preferencesDataStore: PreferencesDataStore

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    private val args: CityDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityDetailsBinding.inflate(inflater, container, false)

        hourlyAdapter = HourlyAdapter()

        binding.rvHourlyList.apply {
            adapter = hourlyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        dailyAdapter = DailyAdapter()

        binding.rvDailyList.apply {
            adapter = dailyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val latitude = args.latitude.toDouble()
        val longitude = args.longitude.toDouble()

        sharedViewModel.current.observe(viewLifecycleOwner) { currentWeatherResponse ->
            if (currentWeatherResponse != null) {
                updateCurrentWeather()
            }
        }

        sharedViewModel.hourlyDaily.observe(viewLifecycleOwner) { oneCallWeatherResponse ->
            if (oneCallWeatherResponse != null) {
                val lat = oneCallWeatherResponse.latitude
                val lon = oneCallWeatherResponse.longitude
                val dailyWeatherList = oneCallWeatherResponse.daily.map { dailyWeatherResponse ->
                    dailyWeatherMapper.mapToDailyWeatherEntity(dailyWeatherResponse, lat, lon)
                }
                dailyAdapter.submitList(dailyWeatherList)

                val hourlyWeatherList = oneCallWeatherResponse.hourly.map { hourlyWeatherResponse ->
                    hourlyWeatherMapper.mapToHourlyWeatherEntity(hourlyWeatherResponse, lat, lon)
                }
                hourlyAdapter.submitList(hourlyWeatherList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val unit = preferencesDataStore.weatherUnitFlow.first()
            sharedViewModel.getCurrentData(latitude, longitude, unit)
            sharedViewModel.getHourlyDailyWeatherData(
                latitude,
                longitude,
                "current,minutely,alerts",
                unit
            )
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateCurrentWeather() {
        sharedViewModel.current.observe(viewLifecycleOwner) { currentWeatherResponse ->
            if (currentWeatherResponse != null) {
                binding.root.setBackgroundResource(getBackgroundResource(currentWeatherResponse.weather[0].id))
                binding.tvLocation.text =
                    "${currentWeatherResponse.name}, ${currentWeatherResponse.sys.country}"
                binding.tvTemperature.text =
                    getString(
                        R.string.celsius_symbol,
                        currentWeatherResponse.main.temp.roundToInt()
                    )
                binding.tvStatus.text = currentWeatherResponse.weather[0].description.replaceFirstChar { it.uppercase()}
                binding.tvTempMin.text =
                    getString(
                        R.string.celsius_symbol,
                        currentWeatherResponse.main.tempMin.roundToInt()
                    )
                binding.tvTempMax.text =
                    getString(
                        R.string.celsius_symbol,
                        currentWeatherResponse.main.tempMax.roundToInt()
                    )
                binding.tvSunrise.text =
                    currentWeatherResponse.sys.sunrise.unixTimestampToTimeString()
                binding.tvSunset.text =
                    currentWeatherResponse.sys.sunset.unixTimestampToTimeString()
                binding.tvFeelsLike.text =
                    getString(
                        R.string.celsius_symbol,
                        currentWeatherResponse.main.feelsLike.roundToInt()
                    )
                binding.tvHumidity.text =
                    getString(R.string.percent_symbol, currentWeatherResponse.main.humidity)
                binding.tvVisibility.text = getString(R.string.km,currentWeatherResponse.visibility / 1000)
                binding.tvWindDegSpeed.text = "${WindDeg.toTextualDescription(currentWeatherResponse.wind.deg)}, ${"%.2f".format(currentWeatherResponse.wind.speed)} ${getString(R.string.ms)}"
                binding.tvPressure.text =
                    getString(R.string.hPa, currentWeatherResponse.main.pressure)
                binding.tvClouds.text =
                    getString(R.string.percent_symbol, currentWeatherResponse.clouds.all)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

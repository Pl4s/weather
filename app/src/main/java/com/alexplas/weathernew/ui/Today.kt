package com.alexplas.weathernew.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import android.Manifest
import com.alexplas.weathernew.R
import com.alexplas.weathernew.adapters.DailyAdapter
import com.alexplas.weathernew.adapters.HourlyAdapter
import com.alexplas.weathernew.data.mappers.DailyWeatherMapper
import com.alexplas.weathernew.data.mappers.HourlyWeatherMapper
import com.alexplas.weathernew.databinding.FragmentTodayBinding
import com.alexplas.weathernew.utils.*
import com.alexplas.weathernew.viewmodel.MyViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class Today : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MyViewModel by activityViewModels()

    private lateinit var locationPermissionHandler: LocationPermissionHandler

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var hourlyWeatherMapper: HourlyWeatherMapper

    @Inject
    lateinit var dailyWeatherMapper: DailyWeatherMapper

    @Inject
    lateinit var preferencesDataStore: PreferencesDataStore

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationPermissionHandler =
            LocationPermissionHandler(this) // create instance of LocationPermissionHandler
        locationPermissionHandler.checkAndRequestPermission() // check and request permission

        hideLayout()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        sharedViewModel.current.observe(viewLifecycleOwner) { currentWeatherResponse ->
            if (currentWeatherResponse != null) {
                updateCurrentWeather()
                showMainLayout()
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fetchWeatherData()
                }
            }
        }

        binding.pullToRefreshFragmentToday.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.fToday.visibility = View.GONE
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatitude = location.latitude
                    val currentLongitude = location.longitude
                    viewLifecycleOwner.lifecycleScope.launch {
                        val unit = preferencesDataStore.weatherUnitFlow.first()
                        sharedViewModel.refreshWeather(currentLatitude, currentLongitude, "minutely,alerts", unit)
                    }
                }
                binding.fToday.visibility = View.VISIBLE
                binding.pullToRefreshFragmentToday.isRefreshing = false
            }
        } else {
            // Handle the case when permission is not granted
            Toast.makeText(
                requireContext(),
                "Location permission is required to refresh the weather data.",
                Toast.LENGTH_SHORT
            ).show()
            binding.pullToRefreshFragmentToday.isRefreshing = false
        }
    }


    private fun fetchWeatherData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val unit = preferencesDataStore.weatherUnitFlow.first()
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLatitude = location.latitude
                            val currentLongitude = location.longitude
                            if (isNetworkAvailable(requireContext())) {
                                sharedViewModel.getCurrentData(currentLatitude, currentLongitude,unit)
                                sharedViewModel.getHourlyDailyWeatherData(
                                    currentLatitude,
                                    currentLongitude,
                                    "current,minutely,alerts",
                                    unit
                                    )
                            } else {
                                // Fetch and display cached data from local storage
                                sharedViewModel.getLocalCurrentData()
                                sharedViewModel.getLocalHourlyDailyData()
                                // Display toast when the internet is not available
                                Toast.makeText(
                                    requireContext(),
                                    "No internet connection!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
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
                binding.tvStatus.text =
                    currentWeatherResponse.weather[0].description.replaceFirstChar { it.uppercase() }
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
                binding.tvVisibility.text =
                    getString(R.string.km, currentWeatherResponse.visibility / 1000)
                binding.tvWindDegSpeed.text =
                    "${WindDeg.toTextualDescription(currentWeatherResponse.wind.deg)}, ${
                        "%.2f".format(currentWeatherResponse.wind.speed)
                    } ${getString(R.string.ms)}"
                binding.tvPressure.text =
                    getString(R.string.hPa, currentWeatherResponse.main.pressure)
                binding.tvClouds.text =
                    getString(R.string.percent_symbol, currentWeatherResponse.clouds.all)
            }
        }
    }

    private fun hideLayout() {
        binding.fToday.visibility = View.GONE
    }

    private fun showMainLayout() {
        binding.fToday.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
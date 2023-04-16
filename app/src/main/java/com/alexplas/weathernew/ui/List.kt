package com.alexplas.weathernew.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexplas.weathernew.adapters.CitiesAdapter
import com.alexplas.weathernew.data.model.cityweatherinfo.CityIdentifier
import com.alexplas.weathernew.databinding.FragmentListBinding
import com.alexplas.weathernew.utils.SwipeToDeleteCallback
import com.alexplas.weathernew.utils.roundTo4DecimalPlaces
import com.alexplas.weathernew.viewmodel.MyViewModel
import com.google.android.material.snackbar.Snackbar

class List : Fragment(), SwipeToDeleteCallback.SwipeToDeleteCallbackListener {

    private lateinit var citiesAdapter: CitiesAdapter

    private val sharedViewModel: MyViewModel by activityViewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citiesAdapter = CitiesAdapter()
        binding.rvCitiesList.apply {
            adapter = citiesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        val swipeToDeleteCallback = SwipeToDeleteCallback(this)
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCitiesList)

        sharedViewModel.showSnackbar.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(
                    requireView(),
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        sharedViewModel.savedCities.observe(viewLifecycleOwner) { cities ->
            citiesAdapter.submitList(cities)
        }

        binding.tvCitiesList.setOnClickListener {
            val action = ListDirections.actionListToSearch()
            findNavController().navigate(action)
        }

        citiesAdapter.setOnItemClickListener { savedCity ->
            val action = ListDirections.actionListToCityDetails(savedCity.lon.toFloat(), savedCity.lat.toFloat())
            findNavController().navigate(action)
        }
    }

    private fun showUndoSnackbar(cityIdentifier: CityIdentifier) {
        Snackbar.make(
            requireView(),
            "City removed from list",
            Snackbar.LENGTH_LONG
        ).setAction("Undo") {
            sharedViewModel.restoreCity(cityIdentifier)
        }.show()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is CitiesAdapter.CitiesViewHolder) {
            val pos = viewHolder.adapterPosition
            val savedCity = citiesAdapter.getCityAtPosition(pos)

            val roundedLat = roundTo4DecimalPlaces(savedCity.lat)
            val roundedLon = roundTo4DecimalPlaces(savedCity.lon)

            val cityIdentifier = CityIdentifier(roundedLat,roundedLon,savedCity.id, savedCity.isSaved)
            sharedViewModel.deleteCity(cityIdentifier)
            showUndoSnackbar(cityIdentifier)
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.getSavedCities()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
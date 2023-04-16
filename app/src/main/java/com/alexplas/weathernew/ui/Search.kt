package com.alexplas.weathernew.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexplas.weathernew.adapters.SearchAdapter
import com.alexplas.weathernew.databinding.FragmentSearchBinding
import com.alexplas.weathernew.utils.Constant.MIN_QUERY_LENGTH
import com.alexplas.weathernew.utils.hideKeyboard
import com.alexplas.weathernew.utils.showKeyboard
import com.alexplas.weathernew.viewmodel.MyViewModel

class Search : Fragment() {

    private lateinit var searchAdapter: SearchAdapter

    private val sharedViewModel: MyViewModel by activityViewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.svSearchList.showKeyboard()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter()

        searchAdapter.setOnItemClickListener { city ->
            sharedViewModel.insertCity(city)
        }

        searchAdapter.setOnParentItemClickListener {city ->
            val action = SearchDirections.actionSearchToWeatherDetails( city.lat.toFloat(), city.lon.toFloat())
            findNavController().navigate(action)
        }

        binding.apply {

            rvSearchList.adapter = searchAdapter
            rvSearchList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            tvCancel.setOnClickListener {
                findNavController().popBackStack()
            }

            svSearchList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        hideKeyboard()
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {

                    if (query != null && query.length >= MIN_QUERY_LENGTH) {
                        searchCities(query)
                    }
                    return false
                }
            })

            svSearchList.setOnClickListener {
                showKeyboard()
                svSearchList.onActionViewExpanded()
                svSearchList.requestFocus()
            }
        }
    }

        private fun searchCities(query: String) {
            Log.d("Search", "searchCities called with query: $query")
            sharedViewModel.getCities(query).observe(viewLifecycleOwner, Observer { cities ->
                Log.d("Search", "Cities found: ${cities.size}")
                searchAdapter.submitList(cities)
            })
        }


    override fun onDestroyView() {
        super.onDestroyView()
        requireView().hideKeyboard()
        _binding = null
    }

}





package com.alexplas.weathernew.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexplas.weathernew.data.local.entity.City
import com.alexplas.weathernew.databinding.SearchListBinding
import com.alexplas.weathernew.utils.DiffUtilCallbackSearchCity

class SearchAdapter(
    private var onItemClickListener: ((City) -> Unit)? = null,
    private var onParentItemClickListener: ((City) -> Unit)? = null
) : ListAdapter<City, SearchAdapter.SearchViewHolder>(DiffUtilCallbackSearchCity()) {

    fun setOnItemClickListener(listener: (City) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnParentItemClickListener(listener: (City) -> Unit) {
        onParentItemClickListener = listener
    }

    class SearchViewHolder(val binding: SearchListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchListBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val search = getItem(position)
        bind(holder.binding, search, onItemClickListener, onParentItemClickListener)
    }

    @SuppressLint("SetTextI18n")
    private fun bind(
        binding: SearchListBinding,
        search: City?,
        onItemClickListener: ((City) -> Unit)?,
        onParentItemClickListener: ((City) -> Unit)?
    ) {
        binding.apply {
            tvCitySearchList.text = "${search?.name}, ${search?.country}"
            if (search?.isSaved == 1) {
                ivAddCity.visibility = View.GONE
                tvAdded.visibility = View.VISIBLE
            } else {
                ivAddCity.visibility = View.VISIBLE
                tvAdded.visibility = View.GONE
            }
            ivAddCity.setOnClickListener {
                onItemClickListener?.let { it(search!!) }
                tvAdded.visibility = View.VISIBLE
                ivAddCity.visibility = View.GONE
            }
            root.setOnClickListener {
                onParentItemClickListener?.let { it(search!!) }
            }
        }
    }
}

package com.example.weatherapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.ElementListviewBinding

class CityAdapter(
    private var cities: MutableList<Cities>,
    private val onFavoriteToggle: ((Cities) -> Unit)? = null
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ElementListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding, onFavoriteToggle)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    fun updateCities(newCities: MutableList<Cities>) {
        cities = newCities
        notifyDataSetChanged()
    }

    class CityViewHolder(
        private val binding: ElementListviewBinding,
        private val onFavoriteToggle: ((Cities) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: Cities) {
            binding.cityName.text = city.name
            binding.temperature.text = "${city.temperature}°C"
            binding.windSpeed.text = "${city.windSpeed} km/h"
            binding.favoriteButton.isChecked = city.isFavorite
            binding.favoriteButton.setOnClickListener {
                onFavoriteToggle?.invoke(city)
            }
        }
    }
}



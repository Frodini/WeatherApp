package com.example.weatherapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.ElementListviewBinding

class CityAdapter(
    private var cities: MutableList<Cities>,
    private val onFavoriteToggle: ((Cities) -> Unit)? = null,
    private val onCityClick: ((Cities) -> Unit)? = null
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ElementListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding, onFavoriteToggle, onCityClick)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int = cities.size

    class CityViewHolder(
        private val binding: ElementListviewBinding,
        private val onFavoriteToggle: ((Cities) -> Unit)?,
        private val onCityClick: ((Cities) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: Cities) {
            binding.cityName.text = city.name
            binding.temperature.text = "${city.temperature}°C"
            binding.windSpeed.text = "${city.windSpeed} km/h"
            binding.favoriteButton.isChecked = city.isFavorite

            binding.favoriteButton.setOnClickListener {
                onFavoriteToggle?.invoke(city)
            }
            // Asegura que el clic en el ítem no incluya el botón de favorito
            itemView.setOnClickListener {
                onCityClick?.invoke(city)
            }
        }
    }

    fun updateCities(newCities: MutableList<Cities>) {
        val diffCallback = CityDiffCallback(this.cities, newCities)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.cities.clear()
        this.cities.addAll(newCities)
        diffResult.dispatchUpdatesTo(this)
    }

    class CityDiffCallback(private val oldList: List<Cities>, private val newList: List<Cities>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}




package com.example.weatherapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.beans.Cities
import com.example.weatherapplication.databinding.ElementListviewBinding

// Adapter for managing and displaying a list of city items in a RecyclerView
class CityAdapter(
    private var cities: MutableList<Cities>,  // List of cities to be displayed
    private val onFavoriteToggle: ((Cities) -> Unit)? = null,  // Optional callback for toggling favorite status
    private val onCityClick: ((Cities) -> Unit)? = null  // Optional callback for handling city item clicks
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    // Method to create new ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        // Inflate the layout for individual list items
        val binding = ElementListviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding, onFavoriteToggle, onCityClick)
    }

    // Method to bind data to ViewHolder instances
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        // Bind the city data to the ViewHolder at the specified position
        holder.bind(cities[position])
    }

    // Method to get the total number of city items
    override fun getItemCount(): Int = cities.size

    // ViewHolder class to hold references to individual item views
    class CityViewHolder(
        private val binding: ElementListviewBinding,  // Binding object for the list item layout
        private val onFavoriteToggle: ((Cities) -> Unit)?,  // Optional callback for favorite toggle
        private val onCityClick: ((Cities) -> Unit)?  // Optional callback for item click
    ) : RecyclerView.ViewHolder(binding.root) {

        // Method to bind city data to the views
        fun bind(city: Cities) {
            binding.cityName.text = city.name
            binding.temperature.text = "${city.temperature}°C"
            binding.windSpeed.text = "${city.windSpeed} km/h"
            binding.favoriteButton.isChecked = city.isFavorite

            // Set a click listener on the favorite button
            binding.favoriteButton.setOnClickListener {
                onFavoriteToggle?.invoke(city)
            }
            // Set a click listener on the entire item view, excluding the favorite button
            itemView.setOnClickListener {
                onCityClick?.invoke(city)
            }
        }
    }

    // Method to update the list of cities and notify the adapter of changes
    fun updateCities(newCities: MutableList<Cities>) {
        // Create a DiffUtil callback to calculate the difference between old and new lists
        val diffCallback = CityDiffCallback(this.cities, newCities)
        // Calculate the diff result
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Clear the old list and add all items from the new list
        this.cities.clear()
        this.cities.addAll(newCities)
        // Notify the adapter of the changes
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil callback class to optimize list updates
    class CityDiffCallback(private val oldList: List<Cities>, private val newList: List<Cities>) : DiffUtil.Callback() {

        // Get the size of the old list
        override fun getOldListSize(): Int {
            return oldList.size
        }

        // Get the size of the new list
        override fun getNewListSize(): Int {
            return newList.size
        }

        // Check if items are the same based on a unique identifier (city name)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        // Check if the contents of items are the same
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

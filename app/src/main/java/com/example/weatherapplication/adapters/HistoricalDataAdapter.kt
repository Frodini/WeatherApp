package com.example.weatherapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.database.WeatherData

// Adapter for displaying historical weather data in a RecyclerView
class HistoricalDataAdapter(private var dataList: List<WeatherData>) : RecyclerView.Adapter<HistoricalDataAdapter.DataViewHolder>() {

    // ViewHolder class to hold references to individual item views
    class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_historical_data, parent, false)) {

        // TextViews to display the city name, temperature, and wind speed
        private var mCityNameView: TextView = itemView.findViewById(R.id.tvCityNameItem)
        private var mTemperatureView: TextView = itemView.findViewById(R.id.tvTemperatureItem)
        private var mWindSpeedView: TextView = itemView.findViewById(R.id.tvWindSpeedItem)

        // Method to bind weather data to the views
        fun bind(data: WeatherData) {
            // Set the city name
            mCityNameView.text = data.cityName
            // Set the temperature with the appropriate unit
            mTemperatureView.text = "${data.temperature} ºC"
            // Set the wind speed with the appropriate unit
            mWindSpeedView.text = "${data.windSpeed} km/h"
        }
    }

    // Method to create new ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        // Inflate the layout for individual list items
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater, parent)
    }

    // Method to bind data to ViewHolder instances
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        // Get the weather data at the specified position
        val data: WeatherData = dataList[position]
        // Bind the weather data to the ViewHolder
        holder.bind(data)
    }

    // Method to get the total number of weather data items
    override fun getItemCount(): Int = dataList.size

    // Method to update the list of weather data and notify the adapter of changes
    fun updateData(newData: List<WeatherData>) {
        // Update the data list with the new data
        dataList = newData
        // Notify the adapter that the data set has changed
        notifyDataSetChanged()
    }
}

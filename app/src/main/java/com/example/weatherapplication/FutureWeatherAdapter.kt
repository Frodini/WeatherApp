package com.example.weatherapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FutureWeatherAdapter(private var futureWeatherList: List<FutureWeatherData>) :
    RecyclerView.Adapter<FutureWeatherAdapter.FutureWeatherViewHolder>() {

    class FutureWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        val tvWindSpeed: TextView = itemView.findViewById(R.id.tvWindSpeed)
        val tvHumidity: TextView = itemView.findViewById(R.id.tvHumidity)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureWeatherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_future_weather_adapter, parent, false)
        return FutureWeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FutureWeatherViewHolder, position: Int) {
        val weatherData = futureWeatherList[position]
        holder.tvDate.text = weatherData.date
        holder.tvTemperature.text = "${weatherData.temperature} ºC"
        holder.tvWindSpeed.text = "Wind Speed: ${weatherData.windSpeed} km/h"
        holder.tvHumidity.text = "Humidity: ${weatherData.humidity} %"
        holder.tvDescription.text = weatherData.description
    }

    override fun getItemCount() = futureWeatherList.size

    fun updateData(newData: List<FutureWeatherData>) {
        futureWeatherList = newData
        notifyDataSetChanged()
    }
}

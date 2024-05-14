package com.example.weatherapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoricalDataAdapter(private var dataList: List<WeatherData>) : RecyclerView.Adapter<HistoricalDataAdapter.DataViewHolder>() {

    class DataViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_historical_data, parent, false)) {
        private var mCityNameView: TextView = itemView.findViewById(R.id.tvCityNameItem)
        private var mTemperatureView: TextView = itemView.findViewById(R.id.tvTemperatureItem)
        private var mWindSpeedView: TextView = itemView.findViewById(R.id.tvWindSpeedItem)

        fun bind(data: WeatherData) {
            mCityNameView.text = data.cityName
            mTemperatureView.text = "${data.temperature} ºC"
            mWindSpeedView.text = "${data.windSpeed} km/h"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data: WeatherData = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<WeatherData>) {
        dataList = newData
        notifyDataSetChanged()
    }

}


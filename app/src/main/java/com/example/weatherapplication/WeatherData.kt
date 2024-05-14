package com.example.weatherapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String?,
    val temperature: Double,
    val windSpeed: Double
)

package com.example.weatherapplication

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "weather_data",
    primaryKeys = ["cityName", "reportDate", "reportTime"],
    indices = [Index(value = ["cityName", "reportDate", "reportTime"], unique = true)]
)
data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Double,
    val precipProbability: Double,
    val cloudCover: Double,
    val description: String,
    val reportDate: String, // Fecha del reporte
    val reportTime: String  // Hora del reporte
)

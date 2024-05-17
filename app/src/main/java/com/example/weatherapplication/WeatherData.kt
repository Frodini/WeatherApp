package com.example.weatherapplication

import androidx.room.Entity
import androidx.room.Index

// Define the WeatherData entity for the Room database
@Entity(
    tableName = "weather_data", // Name of the table in the database
    primaryKeys = ["cityName", "reportDate", "reportTime"], // Composite primary key
    indices = [Index(value = ["cityName", "reportDate", "reportTime"], unique = true)] // Ensure uniqueness of these columns
)
data class WeatherData(
    val cityName: String, // Name of the city for which the weather data is recorded
    val temperature: Double, // Temperature recorded
    val windSpeed: Double, // Wind speed recorded
    val humidity: Double, // Humidity recorded
    val precipProbability: Double, // Probability of precipitation recorded
    val cloudCover: Double, // Cloud cover recorded
    val description: String, // Description of the weather conditions
    val reportDate: String, // Date of the weather report
    val reportTime: String  // Time of the weather report
)

package com.example.weatherapplication.beans

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

data class FutureWeatherData(
    val date: String,
    val temperature: Double,
    val windSpeed: Double,
    val humidity: Double,
    val description: String
)
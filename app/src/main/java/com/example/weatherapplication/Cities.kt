package com.example.weatherapplication

data class Cities(
    val name: String,
    var isFavorite: Boolean = false,
    val temperature: Double,
    val windSpeed: Double
)



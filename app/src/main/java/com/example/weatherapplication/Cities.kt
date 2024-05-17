package com.example.weatherapplication

data class Cities(
    val name: String,
    var isFavorite: Boolean,
    var temperature: Double,
    var windSpeed: Double,
    var humidity: Double = 0.0,
    var precipProbability: Double = 0.0,
    var cloudCover: Double = 0.0,
    var description: String = ""
)

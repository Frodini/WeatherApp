package com.example.weatherapplication

data class Cities(
    val name: String,
    var isFavorite: Boolean = false,
    var temperature: Double? = null,
    var windSpeed: Double? = null
)



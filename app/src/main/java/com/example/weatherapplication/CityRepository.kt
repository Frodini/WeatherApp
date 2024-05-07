package com.example.weatherapplication

object CityRepository {
    private val cities = mutableListOf(
        Cities("Zaragoza", false, 16.0, 5.0),
        Cities("Madrid", true, 20.0, 4.0),
        Cities("Barcelona", false, 18.0, 3.0),
        Cities("Valencia", true, 22.0, 4.5),
        Cities("Sevilla", false, 28.0, 2.0),
        Cities("Bilbao", false, 15.0, 5.0),
        Cities("Oviedo", true, 17.0, 3.5),
        Cities("Málaga", false, 25.0, 3.0),
        Cities("Murcia", false, 24.0, 4.0),
        Cities("Huesca", true, 21.0, 6.0)
    )

    private var onCitiesUpdated: (() -> Unit)? = null

    fun getAllCities(): List<Cities> = cities

    fun getFavoriteCities(): List<Cities> = cities.filter { it.isFavorite }

    fun updateCity(city: Cities) {
        val index = cities.indexOfFirst { it.name == city.name }
        if (index != -1) {
            cities[index] = city
            onCitiesUpdated?.invoke()  // Llamar al callback cuando se actualiza una ciudad
        }
    }

    fun setOnCitiesUpdatedListener(callback: (() -> Unit)?) {
        onCitiesUpdated = callback
    }
}
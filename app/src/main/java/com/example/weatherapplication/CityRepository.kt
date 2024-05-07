package com.example.weatherapplication

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CityRepository {
    /*private val cities = mutableListOf(
        Cities("Huesca", false),
        Cities("Zaragoza", false),
        Cities("Barcelona", false),
        Cities("Valencia", false),
        Cities("Sevilla", false),
        Cities("Bilbao", false),
        Cities("Oviedo", false),
        Cities("Málaga", false),
        Cities("Murcia", false),
        Cities("Madrid", false)
    )*/

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

    private val cities = mutableListOf<Cities>()

    init {
        initializeCities()
    }

    private fun initializeCities() {
        val cityNames = listOf("Zaragoza", "Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao", "Oviedo", "Málaga", "Murcia", "Huesca")
        cityNames.forEach {
            // Añade ciudades con valores predeterminados que serán actualizados
            cities.add(Cities(it, false, 0.0, 0.0))
        }
        fetchWeatherData("YOUR_API_KEY")
    }

    fun fetchWeatherData(apiKey: String) {
        Thread {
            cities.forEach { city ->
                try {
                    val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city.name.replace(" ", "%20")}?unitGroup=metric&key=$apiKey")
                    (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        inputStream.bufferedReader().use { reader ->
                            val response = reader.readText()
                            val jsonObject = JSONObject(response)
                            val days = jsonObject.getJSONArray("days")
                            val todayWeather = days.getJSONObject(0)
                            val temperature = todayWeather.getDouble("temp")
                            val windSpeed = todayWeather.getDouble("windspeed")

                            city.temperature = temperature
                            city.windSpeed = windSpeed
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            onCitiesUpdated?.invoke()
        }.start()
    }
}


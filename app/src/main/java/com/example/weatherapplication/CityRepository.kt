package com.example.weatherapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CityRepository {

    private var onCitiesUpdated: (() -> Unit)? = null
    private lateinit var sharedPreferences: SharedPreferences

    fun getAllCities(): List<Cities> = cities

    fun getFavoriteCities(): List<Cities> = cities.filter { it.isFavorite }

    fun updateCity(city: Cities) {
        val index = cities.indexOfFirst { it.name == city.name }
        if (index != -1) {
            cities[index] = city
            sharedPreferences.edit().putBoolean(city.name, city.isFavorite).apply()
            onCitiesUpdated?.invoke()
        }
    }

    fun setOnCitiesUpdatedListener(callback: (() -> Unit)?) {
        onCitiesUpdated = callback
    }

    private val cities = mutableListOf<Cities>()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        initializeCities()
    }

    private fun initializeCities() {
        cities.clear()  // Limpia la lista antes de añadir elementos
        val cityNames = listOf("Zaragoza", "Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao", "Oviedo", "Málaga", "Murcia", "Huesca")
        cityNames.forEach {
            val isFavorite = sharedPreferences.getBoolean(it, false)
            cities.add(Cities(it, isFavorite, 0.0, 0.0))
        }
        fetchWeatherData("THKTHG25EQ4QFULENB9VYT3D8")
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
                            val humidity = if (todayWeather.has("humidity")) todayWeather.getDouble("humidity") else 0.0
                            val precipProbability = if (todayWeather.has("precipprob")) todayWeather.getDouble("precipprob") else 0.0
                            val cloudCover = if (todayWeather.has("cloudcover")) todayWeather.getDouble("cloudcover") else 0.0
                            val description = if (todayWeather.has("description")) todayWeather.getString("description") else ""

                            Log.d("Respuesta del servidor", "$response")

                            city.temperature = temperature
                            city.windSpeed = windSpeed
                            city.humidity = humidity
                            city.precipProbability = precipProbability
                            city.cloudCover = cloudCover
                            city.description = description
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

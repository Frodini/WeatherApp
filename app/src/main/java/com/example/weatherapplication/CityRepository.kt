package com.example.weatherapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Singleton object to manage city data and weather information
object CityRepository {

    // Callback to notify when cities data is updated
    private var onCitiesUpdated: (() -> Unit)? = null

    // SharedPreferences to store and retrieve favorite city information
    private lateinit var sharedPreferences: SharedPreferences

    // Method to get the list of all cities
    fun getAllCities(): List<Cities> = cities

    // Method to get the list of favorite cities
    fun getFavoriteCities(): List<Cities> = cities.filter { it.isFavorite }

    // Method to update a city's information
    fun updateCity(city: Cities) {
        // Find the index of the city in the list
        val index = cities.indexOfFirst { it.name == city.name }
        if (index != -1) {
            // Update the city information in the list
            cities[index] = city
            // Save the favorite status to SharedPreferences
            sharedPreferences.edit().putBoolean(city.name, city.isFavorite).apply()
            // Invoke the callback to notify about the update
            onCitiesUpdated?.invoke()
        }
    }

    // Method to set the listener for cities data updates
    fun setOnCitiesUpdatedListener(callback: (() -> Unit)?) {
        onCitiesUpdated = callback
    }

    // List to store city objects
    private val cities = mutableListOf<Cities>()

    // Method to initialize the repository with context
    fun init(context: Context) {
        // Initialize SharedPreferences
        sharedPreferences = context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        // Initialize the list of cities
        initializeCities()
    }

    // Method to initialize the list of cities with default data
    private fun initializeCities() {
        // Clear the existing list of cities
        cities.clear()
        // List of city names
        val cityNames = listOf("Zaragoza", "Madrid", "Barcelona", "Valencia", "Sevilla", "Bilbao", "Oviedo", "Málaga", "Murcia", "Huesca")
        // Add each city to the list with default values
        cityNames.forEach {
            val isFavorite = sharedPreferences.getBoolean(it, false)
            cities.add(Cities(it, isFavorite, 0.0, 0.0))
        }
        // Fetch weather data for the cities
        fetchWeatherData("THKTHG25EQ4QFULENB9VYT3D8")
    }

    // Method to fetch weather data from the API
    fun fetchWeatherData(apiKey: String) {
        // Start a new thread to perform network operations
        Thread {
            cities.forEach { city ->
                try {
                    // Construct the API URL with the city name and API key
                    val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/${city.name.replace(" ", "%20")}?unitGroup=metric&key=$apiKey")
                    (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        // Read the response from the API
                        inputStream.bufferedReader().use { reader ->
                            val response = reader.readText()
                            // Parse the JSON response
                            val jsonObject = JSONObject(response)
                            val days = jsonObject.getJSONArray("days")
                            val todayWeather = days.getJSONObject(0)
                            val temperature = todayWeather.getDouble("temp")
                            val windSpeed = todayWeather.getDouble("windspeed")
                            val humidity = if (todayWeather.has("humidity")) todayWeather.getDouble("humidity") else 0.0
                            val precipProbability = if (todayWeather.has("precipprob")) todayWeather.getDouble("precipprob") else 0.0
                            val cloudCover = if (todayWeather.has("cloudcover")) todayWeather.getDouble("cloudcover") else 0.0
                            val description = if (todayWeather.has("description")) todayWeather.getString("description") else ""

                            Log.d("Server Response", "$response")

                            // Update the city's weather information
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
            // Invoke the callback to notify about the update
            onCitiesUpdated?.invoke()
        }.start()
    }

    fun fetchFutureWeatherData(cityName: String, apiKey: String, callback: (List<FutureWeatherData>) -> Unit) {
        Thread {
            val futureWeatherList = mutableListOf<FutureWeatherData>()
            try {
                val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$cityName?unitGroup=metric&key=$apiKey")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    inputStream.bufferedReader().use { reader ->
                        val response = reader.readText()
                        val jsonObject = JSONObject(response)
                        val days = jsonObject.getJSONArray("days")

                        for (i in 1..5) {  // Get the next 5 days
                            val dayWeather = days.getJSONObject(i)
                            val date = dayWeather.getString("datetime")
                            val temperature = dayWeather.getDouble("temp")
                            val windSpeed = dayWeather.getDouble("windspeed")
                            val humidity = if (dayWeather.has("humidity")) dayWeather.getDouble("humidity") else 0.0
                            val description = if (dayWeather.has("description")) dayWeather.getString("description") else ""

                            futureWeatherList.add(FutureWeatherData(date, temperature, windSpeed, humidity, description))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            callback(futureWeatherList)
        }.start()
    }
}

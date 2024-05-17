package com.example.weatherapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Activity to display detailed information about a selected city
class CitiesDetailsActivity : AppCompatActivity() {

    private lateinit var futureWeatherAdapter: FutureWeatherAdapter

    // Suppress lint warning for missing inflated id (to be used with view binding or findViewById)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_details)

        // Retrieve data passed to the activity from the intent
        val cityName = intent.getStringExtra("city_name") ?: "Unknown City"
        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val windSpeed = intent.getDoubleExtra("windSpeed", 0.0)
        val humidity = intent.getDoubleExtra("humidity", 0.0)
        val precipProbability = intent.getDoubleExtra("precipProbability", 0.0)
        val cloudCover = intent.getDoubleExtra("cloudCover", 0.0)
        val description = intent.getStringExtra("description") ?: "No Description"
        val isFavorite = intent.getBooleanExtra("is_favorite", false)

        // Get the current date and time, rounding the current hour
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        val currentTime = SimpleDateFormat("HH:00:00", Locale.getDefault()).format(Calendar.getInstance().time)

        // Log the city name for debugging purposes
        Log.d("CIUDAD ORIGEN", "La ciudad es $cityName")

        // Set the retrieved data in the respective views
        findViewById<TextView>(R.id.tvCityName).text = cityName
        findViewById<TextView>(R.id.tvTemperature).text = "Temperature: $temperature ºC"
        findViewById<TextView>(R.id.tvWindSpeed).text = "Wind Speed: $windSpeed km/h"
        findViewById<TextView>(R.id.tvHumidity).text = "Humidity: $humidity %"
        findViewById<TextView>(R.id.tvPrecipProbability).text = "Precipitation Probability: $precipProbability %"
        findViewById<TextView>(R.id.tvCloudCover).text = "Cloud Cover: $cloudCover %"
        findViewById<TextView>(R.id.tvDescription).text = description
        findViewById<TextView>(R.id.tvReportDate).text = currentDate
        findViewById<TextView>(R.id.tvReportTime).text = currentTime
        findViewById<CheckBox>(R.id.cbFavorite).isChecked = isFavorite

        // Set the dynamic background based on the city name
        val mainLayout: LinearLayout = findViewById(R.id.mainLayout)
        val backgroundResource = when (cityName.toLowerCase(Locale.ROOT)) {
            "zaragoza" -> R.drawable.background_zaragoza
            "madrid" -> R.drawable.background_madrid
            "barcelona" -> R.drawable.background_barcelona
            "valencia" -> R.drawable.background_valencia
            "sevilla" -> R.drawable.background_sevilla
            "bilbao" -> R.drawable.background_bilbao
            "oviedo" -> R.drawable.background_oviedo
            "málaga" -> R.drawable.background_malaga
            "murcia" -> R.drawable.background_murcia
            "huesca" -> R.drawable.background_huesca
            else -> R.drawable.default_background // Default background if no match is found
        }
        mainLayout.setBackgroundResource(backgroundResource)

        // Intent to navigate to the Historical Data Activity
        val intentToHistoricalData = Intent(this, HistoricalDataActivity::class.java).apply {
            putExtra("city_name", cityName)
            putExtra("report_date", currentDate)
            putExtra("report_time", currentTime)
        }
        // Set the click listener for the historical data button
        findViewById<Button>(R.id.btnHistoricalData).setOnClickListener {
            startActivity(intentToHistoricalData)
        }

        // Listener for the save current data button
        findViewById<Button>(R.id.btnSaveCurrentData).setOnClickListener {
            // Create a WeatherData object with the current weather data
            val weatherData = WeatherData(
                cityName = cityName,
                temperature = temperature,
                windSpeed = windSpeed,
                humidity = humidity,
                precipProbability = precipProbability,
                cloudCover = cloudCover,
                description = description,
                reportDate = currentDate,
                reportTime = currentTime
            )
            // Save the weather data to the database
            saveWeatherData(weatherData)
        }

        // Initialize RecyclerView for future weather report
        val rvFutureWeather = findViewById<RecyclerView>(R.id.rvFutureWeather)
        rvFutureWeather.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list initially
        futureWeatherAdapter = FutureWeatherAdapter(emptyList())
        rvFutureWeather.adapter = futureWeatherAdapter

        // Fetch future weather data from the API
        CityRepository.fetchFutureWeatherData(cityName, "X4L4EFE3SE4UUWFRSNTVRHWWB") { futureWeatherList ->
            runOnUiThread {
                futureWeatherAdapter.updateData(futureWeatherList)
            }
        }
    }

    // Method to save weather data to the database using coroutines
    private fun saveWeatherData(weatherData: WeatherData) {
        // Launch a coroutine on the lifecycle scope
        lifecycleScope.launch {
            // Get the DAO for weather data
            val dao = AppDatabase.getDatabase(this@CitiesDetailsActivity).weatherDataDao()
            // Insert the weather data into the database
            dao.insert(weatherData)
            // Show a toast message on the UI thread to confirm data save
            runOnUiThread {
                Toast.makeText(this@CitiesDetailsActivity, "Datos guardados para ${weatherData.cityName}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

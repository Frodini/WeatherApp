package com.example.weatherapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CitiesDetailsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_details)

        // Recuperar datos pasados a la actividad
        val cityName = intent.getStringExtra("city_name") ?: "Unknown City"
        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val windSpeed = intent.getDoubleExtra("windSpeed", 0.0)
        val humidity = intent.getDoubleExtra("humidity", 0.0)
        val precipProbability = intent.getDoubleExtra("precipProbability", 0.0)
        val cloudCover = intent.getDoubleExtra("cloudCover", 0.0)
        val description = intent.getStringExtra("description") ?: "No Description"
        val isFavorite = intent.getBooleanExtra("is_favorite", false)

        Log.d("CIUDAD ORIGEN", "La ciudad es $cityName")

        // Establecer los datos recuperados en las vistas
        findViewById<TextView>(R.id.tvCityName).text = cityName
        findViewById<TextView>(R.id.tvTemperature).text = "$temperature ºC"
        findViewById<TextView>(R.id.tvWindSpeed).text = "$windSpeed km/h"
        findViewById<TextView>(R.id.tvHumidity).text = "$humidity %"
        findViewById<TextView>(R.id.tvPrecipProbability).text = "$precipProbability %"
        findViewById<TextView>(R.id.tvCloudCover).text = "$cloudCover %"
        findViewById<TextView>(R.id.tvDescription).text = description
        findViewById<CheckBox>(R.id.cbFavorite).isChecked = isFavorite

        val intentToHistoricalData = Intent(this, HistoricalDataActivity::class.java)
        intentToHistoricalData.putExtra("city_name", cityName)
        findViewById<Button>(R.id.btnHistoricalData).setOnClickListener {
            startActivity(intentToHistoricalData)
        }

        // Listener para el botón de guardar datos
        val saveButton: Button = findViewById(R.id.btnSaveCurrentData)
        saveButton.setOnClickListener {
            // Usar los datos recuperados para guardar en la base de datos
            val weatherData = WeatherData(
                cityName = cityName,
                temperature = temperature,
                windSpeed = windSpeed,
                humidity = humidity,
                precipProbability = precipProbability,
                cloudCover = cloudCover,
                description = description
            )
            saveWeatherData(weatherData)
        }
    }

    private fun saveWeatherData(weatherData: WeatherData) {
        lifecycleScope.launch {
            AppDatabase.getDatabase(this@CitiesDetailsActivity).weatherDataDao().insert(weatherData)
            runOnUiThread {
                Toast.makeText(this@CitiesDetailsActivity, "Datos guardados para ${weatherData.cityName}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

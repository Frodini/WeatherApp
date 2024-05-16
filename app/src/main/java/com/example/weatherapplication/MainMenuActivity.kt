package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapplication.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verifica el color de fondo de la actividad
        binding.main.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        configureWindowInsets()
        configureListeners()

        // Retrieve and display the saved data
        getSavedCityData()
        getSavedWeatherData()
    }

    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configureListeners() {
        binding.btnFavoriteCities.setOnClickListener {
            startActivity(Intent(this, FavoriteCitiesListActivity::class.java))
        }

        binding.btnGeneralCities.setOnClickListener {
            startActivity(Intent(this, CitiesListActivity::class.java))
        }
    }

    private fun getSavedWeatherData() {
        val sharedPreferences = getSharedPreferences("WeatherData", MODE_PRIVATE)
        val temperature = sharedPreferences.getFloat("temperature", 0.0f).toDouble()
        val description = sharedPreferences.getString("description", "No description")
        val icon = sharedPreferences.getString("icon", "wind")
        val tempmin = sharedPreferences.getFloat("tempmin",0.0f).toDouble()
        val tempmax = sharedPreferences.getFloat("tempmax",0.0f).toDouble()
        val feelsLike = sharedPreferences.getFloat("feelsLike",0.0f).toDouble()

        // Format the temperature to one decimal place
        val formattedTemperature = String.format("%.1f", temperature)
        val formattedTempMin = String.format("%.1f", tempmin)
        val formattedTempMax = String.format("%.1f", tempmax)
        val formattedFeelsLike = String.format("%.1f", feelsLike)

        binding.textViewTemperature.text = "$formattedTemperature ºC"
        binding.textViewTempMin.text = "Min: $formattedTempMin ºC"
        binding.textViewTempMax.text = "Max: $formattedTempMax ºC"
        binding.textViewFeelsLike.text = "Feels like: $formattedFeelsLike ºC"
        binding.textViewWeatherDescription.text = "$description"
        if (icon != null) {
            setWeatherIcon(icon)
        }
    }

    private fun getSavedCityData() {
        val sharedPreferences = getSharedPreferences("CityData", MODE_PRIVATE)
        val city = sharedPreferences.getString("city", "Unknown city")
        binding.textViewLocation.text = city
    }
    private fun setWeatherIcon(icon: String) {
        val weatherBackground: ImageView = findViewById(R.id.weatherBackground)
        when (icon) {
            "clear-day" -> weatherBackground.setImageResource(R.drawable.clear_day)
            "clear-night" -> weatherBackground.setImageResource(R.drawable.clear_night)
            "cloudy" -> weatherBackground.setImageResource(R.drawable.cloudy)
            "fog" -> weatherBackground.setImageResource(R.drawable.fog)
            "hail" -> weatherBackground.setImageResource(R.drawable.hail)
            "partly-cloudy-day" -> weatherBackground.setImageResource(R.drawable.partly_cloudy_day)
            "partly-cloudy-night" -> weatherBackground.setImageResource(R.drawable.partly_cloudy_night)
            "rain-snow-showers-day" -> weatherBackground.setImageResource(R.drawable.rain_snow_showers_day)
            "rain-snow-showers-night" -> weatherBackground.setImageResource(R.drawable.rain_snow_showers_night)
            "rain-snow" -> weatherBackground.setImageResource(R.drawable.rain_snow)
            "rain" -> weatherBackground.setImageResource(R.drawable.rain)
            "showers-day" -> weatherBackground.setImageResource(R.drawable.showers_day)
            "showers-night" -> weatherBackground.setImageResource(R.drawable.showers_night)
            "sleet" -> weatherBackground.setImageResource(R.drawable.sleet)
            "snow-showers-day" -> weatherBackground.setImageResource(R.drawable.snow_showers_day)
            "snow-showers-night" -> weatherBackground.setImageResource(R.drawable.snow_showers_night)
            "snow" -> weatherBackground.setImageResource(R.drawable.snow)
            "thunder-rain" -> weatherBackground.setImageResource(R.drawable.thunder_rain)
            "thunder-showers-day" -> weatherBackground.setImageResource(R.drawable.thunder_showers_day)
            "thunder-showers-night" -> weatherBackground.setImageResource(R.drawable.thunder_showers_night)
            "thunder" -> weatherBackground.setImageResource(R.drawable.thunder)
            "wind" -> weatherBackground.setImageResource(R.drawable.wind)
            else -> weatherBackground.setImageResource(R.drawable.wind) // Imagen por defecto
        }
    }
}

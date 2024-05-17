package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.ActivityMainMenuBinding
import org.json.JSONArray
import org.json.JSONObject

// Main activity class for the main menu
class MainMenuActivity : AppCompatActivity() {
    // View binding to access views in the layout
    private lateinit var binding: ActivityMainMenuBinding

    // onCreate method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the background color of the main view
        binding.main.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // Configure window insets for proper padding
        configureWindowInsets()
        // Configure button listeners
        configureListeners()

        // Retrieve and display the saved city and weather data
        getSavedCityData()
        getSavedWeatherData()
        getFutureWeatherData()
    }

    // Method to configure window insets for proper padding
    private fun configureWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Method to configure listeners for button clicks
    private fun configureListeners() {
        // Listener for the favorite cities button
        binding.btnFavoriteCities.setOnClickListener {
            startActivity(Intent(this, FavoriteCitiesListActivity::class.java))
        }

        // Listener for the general cities button
        binding.btnGeneralCities.setOnClickListener {
            startActivity(Intent(this, CitiesListActivity::class.java))
        }
    }

    // Method to retrieve and display saved weather data
    private fun getSavedWeatherData() {
        val sharedPreferences = getSharedPreferences("WeatherData", MODE_PRIVATE)
        val temperature = sharedPreferences.getFloat("temperature", 0.0f).toDouble()
        val description = sharedPreferences.getString("description", "No description")
        val icon = sharedPreferences.getString("icon", "wind")
        val tempmin = sharedPreferences.getFloat("tempmin", 0.0f).toDouble()
        val tempmax = sharedPreferences.getFloat("tempmax", 0.0f).toDouble()
        val feelsLike = sharedPreferences.getFloat("feelsLike", 0.0f).toDouble()

        // Format the temperature to one decimal place
        val formattedTemperature = String.format("%.1f", temperature)
        val formattedTempMin = String.format("%.1f", tempmin)
        val formattedTempMax = String.format("%.1f", tempmax)
        val formattedFeelsLike = String.format("%.1f", feelsLike)

        // Set the text views with the formatted weather data
        binding.textViewTemperature.text = "$formattedTemperature ºC"
        binding.textViewTempMin.text = "Min: $formattedTempMin ºC"
        binding.textViewTempMax.text = "Max: $formattedTempMax ºC"
        binding.textViewFeelsLike.text = "Feels like: $formattedFeelsLike ºC"
        binding.textViewWeatherDescription.text = "$description"

        // Set the weather icon if it's not null
        if (icon != null) {
            setWeatherIcon(icon)
        }
    }

    // Method to retrieve and display saved city data
    private fun getSavedCityData() {
        val sharedPreferences = getSharedPreferences("CityData", MODE_PRIVATE)
        val city = sharedPreferences.getString("city", "Unknown city")
        binding.textViewLocation.text = city
    }

    // Method to set the appropriate weather icon based on the icon string
    private fun setWeatherIcon(icon: String) {
        // Find the image view to set the weather icon
        val weatherBackground: ImageView = findViewById(R.id.weatherBackground)

        // Set the image resource based on the icon string
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
            else -> weatherBackground.setImageResource(R.drawable.wind) // Default image
        }
    }

    // Method to retrieve and display future weather data
    private fun getFutureWeatherData() {
        val sharedPreferences = getSharedPreferences("FutureWeatherData", MODE_PRIVATE)
        val futureWeatherDataString = sharedPreferences.getString("futureWeather", "[]")
        val futureWeatherJsonArray = JSONArray(futureWeatherDataString)

        val futureWeatherList = mutableListOf<FutureWeatherData>()
        for (i in 0 until futureWeatherJsonArray.length()) {
            val dayJson = futureWeatherJsonArray.getJSONObject(i)
            val dayData = FutureWeatherData(
                date = dayJson.getString("date"),
                temperature = dayJson.getDouble("temperature"),
                windSpeed = dayJson.getDouble("windSpeed"),
                humidity = dayJson.getDouble("humidity"),
                description = dayJson.getString("description")
            )
            futureWeatherList.add(dayData)
        }

        // Initialize the RecyclerView with the future weather data
        binding.recyclerViewFutureWeather.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFutureWeather.adapter = FutureWeatherAdapter(futureWeatherList)
    }
}

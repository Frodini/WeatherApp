package com.example.weatherapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CitiesDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_details)

        val cityName = intent.getStringExtra("city_name")
        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val windSpeed = intent.getDoubleExtra("wind_speed", 0.0)
        val isFavorite = intent.getBooleanExtra("is_favorite", false)

        // Suponiendo que tienes TextViews con estos IDs
        findViewById<TextView>(R.id.tvCityName).text = cityName
        findViewById<TextView>(R.id.tvTemperature).text = "{$temperature}ºC"
        //findViewById<TextView>(R.id.tvWindSpeed).text = getString(R.string.wind_speed, windSpeed)
        //findViewById<CheckBox>(R.id.cbFavorite).isChecked = isFavorite
    }
}

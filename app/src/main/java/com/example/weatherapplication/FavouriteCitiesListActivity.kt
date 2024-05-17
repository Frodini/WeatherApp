package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.ActivityFavouriteCitiesListBinding

// Activity to display a list of favorite cities
class FavoriteCitiesListActivity : AppCompatActivity() {
    // View binding to access views in the layout
    private lateinit var binding: ActivityFavouriteCitiesListBinding

    // Adapter for the RecyclerView to display the cities
    private lateinit var cityAdapter: CityAdapter

    // Current search query for filtering cities by name
    private var currentQuery: String = ""  // Initialize with an empty string

    // onCreate method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityFavouriteCitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch weather data for the cities
        CityRepository.fetchWeatherData("X4L4EFE3SE4UUWFRSNTVRHWWB")
        // Set a listener to update the UI when cities data is updated
        CityRepository.setOnCitiesUpdatedListener {
            runOnUiThread {
                cityAdapter.notifyDataSetChanged()
            }
        }
        // Set up filters for the city list
        setUpFilters()
        // Set up the RecyclerView
        setUpRecyclerView()
    }

    // Method to update the list of favorite cities
    private fun updateFavoriteCities() {
        cityAdapter.updateCities(CityRepository.getFavoriteCities().toMutableList())
        filterCitiesByName(currentQuery)  // Apply the current search query filter
    }

    // onResume method called when the activity is resumed
    override fun onResume() {
        super.onResume()
        // Update the list of favorite cities when the activity is resumed
        updateFavoriteCities()
    }

    // Method to filter cities by name based on the search query
    private fun filterCitiesByName(query: String) {
        val filteredList = if (query.isEmpty()) {
            // Return all favorite cities if query is empty
            CityRepository.getFavoriteCities()
        } else {
            // Filter favorite cities by name containing the query (case insensitive)
            CityRepository.getFavoriteCities().filter { it.name.contains(query, ignoreCase = true) }
        }
        // Update the adapter with the filtered list
        cityAdapter.updateCities(filteredList.toMutableList())
    }

    // Method to set up filters for the city list
    private fun setUpFilters() {
        // Add a text change listener to the search EditText
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Update currentQuery with the current text and apply the filter
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()  // Update currentQuery with the current text
                filterCitiesByName(currentQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up the temperature filter button
        binding.filterTempButton.setOnClickListener {
            // Sort favorite cities by temperature and update the adapter
            val filteredList = CityRepository.getFavoriteCities().sortedBy { it.temperature }
            cityAdapter.updateCities(filteredList.toMutableList())
        }

        // Set up the wind speed filter button
        binding.filterWindButton.setOnClickListener {
            // Sort favorite cities by wind speed and update the adapter
            val filteredList = CityRepository.getFavoriteCities().sortedBy { it.windSpeed }
            cityAdapter.updateCities(filteredList.toMutableList())
        }
    }

    // Method to set up the RecyclerView
    private fun setUpRecyclerView() {
        // Initialize the adapter with favorite cities and listeners for item clicks
        cityAdapter = CityAdapter(
            CityRepository.getFavoriteCities().toMutableList(),
            { city ->
                // Handle the favorite button toggle
                city.isFavorite = !city.isFavorite
                CityRepository.updateCity(city)
                if (!city.isFavorite) {
                    updateFavoriteCities()  // Only update if the city is removed from favorites
                }
            },
            { city -> // Handle item click to open city details
                openCityDetailActivity(city)
            }
        )
        // Set the adapter and layout manager for the RecyclerView
        binding.favoritesRecyclerView.adapter = cityAdapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    // Method to open the city detail activity with the selected city's details
    private fun openCityDetailActivity(city: Cities) {
        // Create an intent to start the CitiesDetailsActivity
        val intent = Intent(this, CitiesDetailsActivity::class.java).apply {
            // Put the city's details as extras in the intent
            putExtra("city_name", city.name)
            putExtra("temperature", city.temperature)
            putExtra("windSpeed", city.windSpeed)  // Assume you have this property
            putExtra("is_favorite", city.isFavorite)
            putExtra("humidity", city.humidity)
            putExtra("precipProbability", city.precipProbability)
            putExtra("cloudCover", city.cloudCover)
            putExtra("description", city.description)
        }
        // Start the CitiesDetailsActivity
        startActivity(intent)
    }
}

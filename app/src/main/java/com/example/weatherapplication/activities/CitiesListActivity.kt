package com.example.weatherapplication.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.beans.Cities
import com.example.weatherapplication.adapters.CityAdapter
import com.example.weatherapplication.beans.CityRepository
import com.example.weatherapplication.databinding.ActivityCitiesListBinding

// Activity to display a list of cities with weather information
class CitiesListActivity : AppCompatActivity() {
    // View binding to access views in the layout
    private lateinit var binding: ActivityCitiesListBinding

    // Adapter for the RecyclerView to display the cities
    private lateinit var cityAdapter: CityAdapter

    // List to hold the cities to be displayed
    private var displayedCities = mutableListOf<Cities>()

    // onCreate method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityCitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the list of cities
        initializeCities()
        // Set up the RecyclerView
        setUpRecyclerView()
        // Fetch weather data for the cities
        CityRepository.fetchWeatherData("R4JYEMDZSGHXLPQH5RYUAG4BG")
        // Set a listener to update the UI when cities data is updated
        CityRepository.setOnCitiesUpdatedListener {
            runOnUiThread {
                cityAdapter.notifyDataSetChanged()
            }
        }
        // Set up filters for the city list
        setUpFilters()
    }

    // Method to initialize the list of cities from the repository
    private fun initializeCities() {
        displayedCities = CityRepository.getAllCities().toMutableList()
    }

    // Method to set up filters for the city list
    private fun setUpFilters() {
        // Add a text change listener to the search EditText
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Filter cities by name as the user types
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCitiesByName(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Set up the temperature filter button
        binding.filterTempButton.setOnClickListener {
            // Sort cities by temperature and update the adapter
            displayedCities.sortBy { it.temperature }
            cityAdapter.notifyDataSetChanged()
        }

        // Set up the wind speed filter button
        binding.filterWindButton.setOnClickListener {
            // Sort cities by wind speed and update the adapter
            displayedCities.sortBy { it.windSpeed }
            cityAdapter.notifyDataSetChanged()
        }
    }

    // Method to filter cities by name based on the query
    private fun filterCitiesByName(query: String) {
        val filteredList = if (query.isEmpty()) {
            // Return all cities if query is empty
            CityRepository.getAllCities()
        } else {
            // Filter cities by name containing the query (case insensitive)
            CityRepository.getAllCities().filter { it.name.contains(query, ignoreCase = true) }
        }
        // Update the adapter with the filtered list
        cityAdapter.updateCities(filteredList.toMutableList())
    }

    // Method to set up the RecyclerView
    private fun setUpRecyclerView() {
        // Initialize the adapter with the displayed cities and listeners for item clicks
        cityAdapter = CityAdapter(displayedCities, { city ->
            // Toggle the favorite status of the city
            val index = displayedCities.indexOf(city)
            city.isFavorite = !city.isFavorite
            // Update the city in the repository
            CityRepository.updateCity(city)
            // Notify the adapter about the change
            cityAdapter.notifyItemChanged(index)
        }, { city ->
            // Open the city detail activity when a city is clicked
            openCityDetailActivity(city)
        })
        // Set the adapter and layout manager for the RecyclerView
        binding.citiesRecyclerView.adapter = cityAdapter
        binding.citiesRecyclerView.layoutManager = LinearLayoutManager(this)
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

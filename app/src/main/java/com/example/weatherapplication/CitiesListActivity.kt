package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.ActivityCitiesListBinding

class CitiesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitiesListBinding
    private lateinit var cityAdapter: CityAdapter
    private var displayedCities = mutableListOf<Cities>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCities()
        setUpRecyclerView()
        setUpFilters()
    }

    private fun initializeCities() {
        displayedCities = CityRepository.getAllCities().toMutableList()
    }

    private fun setUpFilters() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCitiesByName(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.filterTempButton.setOnClickListener {
            displayedCities.sortBy { it.temperature }
            cityAdapter.notifyDataSetChanged()
        }

        binding.filterWindButton.setOnClickListener {
            displayedCities.sortBy { it.windSpeed }
            cityAdapter.notifyDataSetChanged()
        }
    }

    private fun filterCitiesByName(query: String) {
        val filteredList = if (query.isEmpty()) {
            CityRepository.getAllCities()
        } else {
            CityRepository.getAllCities().filter { it.name.contains(query, ignoreCase = true) }
        }
        cityAdapter.updateCities(filteredList.toMutableList())
    }

    private fun setUpRecyclerView() {
        cityAdapter = CityAdapter(displayedCities, { city ->
            val index = displayedCities.indexOf(city)
            city.isFavorite = !city.isFavorite
            CityRepository.updateCity(city)
            cityAdapter.notifyItemChanged(index)
        }, { city ->
            openCityDetailActivity(city)
        })
        binding.citiesRecyclerView.adapter = cityAdapter
        binding.citiesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun openCityDetailActivity(city: Cities) {
        val intent = Intent(this, CitiesDetailsActivity::class.java).apply {
            putExtra("city_name", city.name)
            putExtra("temperature", city.temperature)
            putExtra("wind_speed", city.windSpeed)  // Asume que tienes esta propiedad
            putExtra("is_favorite", city.isFavorite)
        }
        startActivity(intent)
    }

}

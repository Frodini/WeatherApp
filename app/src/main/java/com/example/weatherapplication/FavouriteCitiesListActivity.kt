package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.ActivityFavouriteCitiesListBinding

class FavoriteCitiesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteCitiesListBinding
    private lateinit var cityAdapter: CityAdapter
    private var currentQuery: String = ""  // Inicializa con un string vacío

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteCitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpFilters()  // Configura los filtros
        setUpRecyclerView()  // Configura el RecyclerView
    }
    private fun updateFavoriteCities() {
        cityAdapter.updateCities(CityRepository.getFavoriteCities().toMutableList())
        filterCitiesByName(currentQuery)
    }
    override fun onResume() {
        super.onResume()
        updateFavoriteCities()  // Asegúrate de actualizar cuando la actividad se reanude
    }

    private fun filterCitiesByName(query: String) {
        val filteredList = if (query.isEmpty()) {
            CityRepository.getFavoriteCities()
        } else {
            CityRepository.getFavoriteCities().filter { it.name.contains(query, ignoreCase = true) }
        }
        cityAdapter.updateCities(filteredList.toMutableList())
    }

    private fun setUpFilters() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString()  // Actualiza currentQuery con el texto actual
                filterCitiesByName(currentQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.filterTempButton.setOnClickListener {
            val filteredList = CityRepository.getFavoriteCities().sortedBy { it.temperature }
            cityAdapter.updateCities(filteredList.toMutableList())
        }

        binding.filterWindButton.setOnClickListener {
            val filteredList = CityRepository.getFavoriteCities().sortedBy { it.windSpeed }
            cityAdapter.updateCities(filteredList.toMutableList())
        }
    }

    private fun setUpRecyclerView() {
        cityAdapter = CityAdapter(
            CityRepository.getFavoriteCities().toMutableList(),
            { city ->
                // Manipulación del botón de favoritos
                city.isFavorite = !city.isFavorite
                CityRepository.updateCity(city)
                if (!city.isFavorite) {
                    updateFavoriteCities()  // Solo actualiza si se quita de favoritos
                }
            },
            { city -> // Para abrir detalles
                openCityDetailActivity(city)
            }
        )
        binding.favoritesRecyclerView.adapter = cityAdapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun openCityDetailActivity(city: Cities) {
        val intent = Intent(this, CitiesDetailsActivity::class.java).apply {
            putExtra("city_name", city.name)
            putExtra("temperature", city.temperature)
            putExtra("is_favorite", city.isFavorite)
        }
        startActivity(intent)
    }

}

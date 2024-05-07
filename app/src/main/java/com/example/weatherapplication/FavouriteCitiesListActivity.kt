package com.example.weatherapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.databinding.ActivityFavouriteCitiesListBinding

class FavoriteCitiesListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteCitiesListBinding
    private lateinit var cityAdapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteCitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cityAdapter = CityAdapter(CityRepository.getFavoriteCities().toMutableList()) { city ->
            city.isFavorite = !city.isFavorite
            CityRepository.updateCity(city)
            if (!city.isFavorite) {  // Si la ciudad ya no es favorita, actualiza la lista
                updateFavoriteCities()
            }
        }
        binding.favoritesRecyclerView.adapter = cityAdapter
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateFavoriteCities() {
        cityAdapter.updateCities(CityRepository.getFavoriteCities().toMutableList())
    }

}

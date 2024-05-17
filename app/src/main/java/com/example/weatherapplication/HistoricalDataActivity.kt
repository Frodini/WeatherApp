package com.example.weatherapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoricalDataActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoricalDataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_data)

        recyclerView = findViewById(R.id.recyclerViewHistoricalData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoricalDataAdapter(emptyList())
        recyclerView.adapter = adapter

        val cityName = intent.getStringExtra("city_name")
        Log.d("CIUDAD", "Nombre de la ciudad seleccionada $cityName")
        if (cityName != null) {
            loadHistoricalData(cityName)
        } else {
            Toast.makeText(this, "City name not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadHistoricalData(cityName: String) {
        lifecycleScope.launch {
            val dataList = AppDatabase.getDatabase(this@HistoricalDataActivity).weatherDataDao().getWeatherDataByCity(cityName)
            runOnUiThread {
                if (dataList.isNotEmpty()) {
                    adapter.updateData(dataList) // Asegúrate de que exista este método en tu adapter
                } else {
                    Toast.makeText(this@HistoricalDataActivity, "No historical data found for $cityName", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

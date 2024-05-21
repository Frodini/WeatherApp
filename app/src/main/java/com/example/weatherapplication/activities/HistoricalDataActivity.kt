package com.example.weatherapplication.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.database.AppDatabase
import com.example.weatherapplication.adapters.HistoricalDataAdapter
import com.example.weatherapplication.R
import com.example.weatherapplication.database.WeatherData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

// Activity to display historical weather data for a selected city
class HistoricalDataActivity : AppCompatActivity() {
    // RecyclerView to display historical weather data
    private lateinit var recyclerView: RecyclerView

    // Adapter for the RecyclerView
    private lateinit var adapter: HistoricalDataAdapter

    // TextViews to display average weather data
    private lateinit var tvAverageTemperature: TextView
    private lateinit var tvAverageWindSpeed: TextView
    private lateinit var tvAverageHumidity: TextView

    // LineChart to display weather data trends
    private lateinit var lineChart: LineChart

    // onCreate method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_data)

        // Initialize the RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewHistoricalData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoricalDataAdapter(emptyList())
        recyclerView.adapter = adapter

        // Initialize the TextViews
        tvAverageTemperature = findViewById(R.id.tvAverageTemperature)
        tvAverageWindSpeed = findViewById(R.id.tvAverageWindSpeed)
        tvAverageHumidity = findViewById(R.id.tvAverageHumidity)

        // Initialize the LineChart
        lineChart = findViewById(R.id.lineChart)

        // Get the city name from the intent
        val cityName = intent.getStringExtra("city_name")
        Log.d("CIUDAD", "Nombre de la ciudad seleccionada $cityName")

        // Load historical data if the city name is provided
        if (cityName != null) {
            loadHistoricalData(cityName)
        } else {
            // Show a toast message if the city name is not provided
            Toast.makeText(this, "City name not provided", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to load historical data from the database
    private fun loadHistoricalData(cityName: String) {
        lifecycleScope.launch {
            // Fetch the historical data for the specified city from the database
            val dataList = AppDatabase.getDatabase(this@HistoricalDataActivity).weatherDataDao().getWeatherDataByCity(cityName)

            // Update the UI on the main thread
            runOnUiThread {
                if (dataList.isNotEmpty()) {
                    // Update the adapter with the fetched data
                    adapter.updateData(dataList)

                    // Calculate and display average weather data
                    calculateAndDisplayAverages(dataList)

                    // Display the weather data trends in the chart
                    displayChart(dataList)
                } else {
                    // Show a toast message if no historical data is found for the city
                    Toast.makeText(this@HistoricalDataActivity, "No historical data found for $cityName", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Method to calculate and display average weather data
    private fun calculateAndDisplayAverages(dataList: List<WeatherData>) {
        // Calculate the average values for temperature, wind speed, and humidity
        val averageTemperature = dataList.map { it.temperature }.average()
        val averageWindSpeed = dataList.map { it.windSpeed }.average()
        val averageHumidity = dataList.map { it.humidity }.average()

        // Display the calculated average values in the TextViews
        tvAverageTemperature.text = "Average Temperature: %.2f ºC".format(averageTemperature)
        tvAverageWindSpeed.text = "Average Wind Speed: %.2f km/h".format(averageWindSpeed)
        tvAverageHumidity.text = "Average Humidity: %.2f %%".format(averageHumidity)
    }

    // Method to display weather data trends in a line chart
    private fun displayChart(dataList: List<WeatherData>) {
        // Create entries for the temperature data points
        val temperatureEntries = dataList.mapIndexed { index, weatherData ->
            Entry(index.toFloat(), weatherData.temperature.toFloat())
        }

        // Create entries for the wind speed data points
        val windSpeedEntries = dataList.mapIndexed { index, weatherData ->
            Entry(index.toFloat(), weatherData.windSpeed.toFloat())
        }

        // Create entries for the humidity data points
        val humidityEntries = dataList.mapIndexed { index, weatherData ->
            Entry(index.toFloat(), weatherData.humidity.toFloat())
        }

        // Create data sets for temperature and wind speed
        val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature").apply {
            setDrawValues(false) // Disable drawing values on the data points
            setDrawCircles(false) // Disable drawing circles on the data points
            lineWidth = 2f // Set the line width
            color = resources.getColor(com.google.android.material.R.color.m3_sys_color_dark_on_primary) // Set the line color
        }

        val windSpeedDataSet = LineDataSet(windSpeedEntries, "Wind Speed").apply {
            setDrawValues(false) // Disable drawing values on the data points
            setDrawCircles(false) // Disable drawing circles on the data points
            lineWidth = 2f // Set the line width
            color = resources.getColor(androidx.appcompat.R.color.abc_btn_colored_text_material) // Set the line color
        }

        // Combine the data sets into a LineData object
        val lineData = LineData(temperatureDataSet, windSpeedDataSet)

        // Set the data for the LineChart and refresh it
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart
    }
}

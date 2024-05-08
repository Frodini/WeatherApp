package com.example.weatherapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.example.weatherapplication.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 120
    }

    private lateinit var binding: ActivityMainMenuBinding
    private var locationRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureWindowInsets()
        configureListeners()
        CityRepository.init(this)
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

        // Solo solicitar la ubicación una vez
        if (!locationRequested) {
            requestLocation()
            locationRequested = true
        }
    }

    private fun requestLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val listener = LocationListener { location ->
            fetchWeatherAndCity(location.latitude, location.longitude)
        }

        if (checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            requestLocation()
        }
    }

    private fun fetchWeatherAndCity(latitude: Double, longitude: Double) {
        for (i in 0..1) {
            obtenerDatosDesdeAPI(latitude, longitude)
            fetchCityName(latitude, longitude)
        }
    }

    private fun obtenerDatosDesdeAPI(latitude: Double, longitude: Double) {
        val apiKey = "THKTHG25EQ4QFULENB9VYT3D8"
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$latitude,$longitude?unitGroup=metric&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val daysArray = jsonObject.getJSONArray("days")
                        val firstDay = daysArray.getJSONObject(0)
                        val temperature = firstDay.getDouble("temp")
                        val description = jsonObject.getString("description")

                        runOnUiThread {
                            binding.textViewTemperature.text = "${temperature}ºC"
                            binding.textViewWeatherDescription.text = "Descripción: $description"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun fetchCityName(latitude: Double, longitude: Double) {
        val apiKey = "f058fe13f46a4614a01fcd1ccc963452"
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$latitude+$longitude&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    val responseBody = resp.body?.string()
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val resultsArray = jsonObject.getJSONArray("results")
                        val firstResult = resultsArray.getJSONObject(0)
                        val city = firstResult.getJSONObject("components").getString("city")

                        runOnUiThread {
                            binding.textViewLocation.text = city
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}

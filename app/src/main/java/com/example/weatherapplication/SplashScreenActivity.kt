package com.example.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 120
    }

    // Declare FusedLocationProviderClient for getting location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Flags to ensure location and data fetch are requested only once
    private var locationRequested = false
    private var weatherDataFetched = false
    private var cityDataFetched = false

    // onCreate method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Start initialization and data loading
        initializeAndLoadData()
    }

    // Method to initialize and load data
    private fun initializeAndLoadData() {
        // Initialize the city repository (for managing city data)
        CityRepository.init(this)

        // Request location if it hasn't been requested yet
        if (!locationRequested) {
            requestLocation()
            locationRequested = true
        }
    }

    // Method to request the user's location
    private fun requestLocation() {
        // Check if location permissions are granted
        if (checkLocationPermission()) {
            Log.d("Location", "Permissions granted, requesting location updates")
            // Get the last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, OnSuccessListener { location ->
                    if (location != null) {
                        Log.d("Location", "Location received: ${location.latitude}, ${location.longitude}")
                        // Fetch weather and city data using the received location
                        fetchWeatherAndCity(location.latitude, location.longitude)
                    } else {
                        Log.e("Location", "Location is null")
                        // Proceed to main menu if location is null
                        proceedToMainMenu()
                    }
                })
                .addOnFailureListener {
                    Log.e("Location", "Failed to get location: ${it.message}")
                    // Proceed to main menu if location request fails
                    proceedToMainMenu()
                }
        } else {
            Log.d("Location", "Permissions not granted")
            // Proceed to main menu if permissions are not granted
            proceedToMainMenu()
        }
    }

    // Method to check if location permissions are granted
    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        // Request permissions if they are not granted
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    // Callback method for handling permission request results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Permissions granted")
                // Request location again if permissions are granted
                requestLocation()
            } else {
                Log.d("Permissions", "Permissions denied")
                Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show()
                // Proceed to main menu if permissions are denied
                proceedToMainMenu()
            }
        }
    }

    // Method to check if network is available
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Method to fetch weather and city data based on location
    private fun fetchWeatherAndCity(latitude: Double, longitude: Double) {
        // Check if network is available
        if (isNetworkAvailable()) {
            // Fetch weather data from the API
            obtenerDatosDesdeAPI(latitude, longitude)
            // Fetch city name from the API
            fetchCityName(latitude, longitude)
        } else {
            Log.e("Network", "No network available")
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            // Proceed to main menu if network is not available
            proceedToMainMenu()
        }
    }

    // Method to fetch weather data from an external API
    private fun obtenerDatosDesdeAPI(latitude: Double, longitude: Double) {
        val apiKey = "X4L4EFE3SE4UUWFRSNTVRHWWB"
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$latitude,$longitude?unitGroup=metric&key=$apiKey"

        Log.d("WeatherAPI", "URL: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("WeatherAPI", "Request failed: ${e.message}")
                // Proceed to main menu if the request fails
                proceedToMainMenu()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("WeatherAPI", "Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val daysArray = jsonObject.getJSONArray("days")
                        val firstDay = daysArray.getJSONObject(0)
                        val temperature = firstDay.getDouble("temp")
                        val description = jsonObject.getString("description")
                        val icon = firstDay.getString("icon")
                        val tempmin = firstDay.getDouble("tempmin")
                        val tempmax = firstDay.getDouble("tempmax")
                        val feelsLike = firstDay.getDouble("feelslike")

                        // Save the fetched weather data
                        saveWeatherData(temperature, description, icon, tempmin, tempmax, feelsLike)

                        weatherDataFetched = true
                        // Proceed to main menu
                        proceedToMainMenu()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("WeatherAPI", "JSON Parsing error: ${e.message}")
                        // Proceed to main menu if JSON parsing fails
                        proceedToMainMenu()
                    }
                } else {
                    Log.e("WeatherAPI", "Response not successful: ${response.code}")
                    // Proceed to main menu if response is not successful
                    proceedToMainMenu()
                }
            }
        })
    }

    // Method to fetch city name from an external API
    private fun fetchCityName(latitude: Double, longitude: Double) {
        val apiKey = "f058fe13f46a4614a01fcd1ccc963452"
        val url = "https://api.opencagedata.com/geocode/v1/json?q=$latitude+$longitude&key=$apiKey"

        Log.d("CityAPI", "URL: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("CityAPI", "Request failed: ${e.message}")
                // Proceed to main menu if the request fails
                proceedToMainMenu()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("CityAPI", "Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val resultsArray = jsonObject.getJSONArray("results")
                        val firstResult = resultsArray.getJSONObject(0)
                        val city = firstResult.getJSONObject("components").optString("city", "Unknown city")

                        // Save the fetched city data
                        saveCityData(city)

                        cityDataFetched = true
                        // Proceed to main menu
                        proceedToMainMenu()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("CityAPI", "JSON Parsing error: ${e.message}")
                        // Proceed to main menu if JSON parsing fails
                        proceedToMainMenu()
                    }
                } else {
                    Log.e("CityAPI", "Response not successful: ${response.code}")
                    // Proceed to main menu if response is not successful
                    proceedToMainMenu()
                }
            }
        })
    }

    // Method to save weather data in shared preferences
    private fun saveWeatherData(temperature: Double, description: String, icon: String, tempmin: Double, tempmax: Double, feelsLike: Double) {
        val sharedPreferences = getSharedPreferences("WeatherData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("temperature", temperature.toFloat())
        editor.putString("description", description)
        editor.putString("icon", icon)
        editor.putFloat("tempmin", tempmin.toFloat())
        editor.putFloat("tempmax", tempmax.toFloat())
        editor.putFloat("feelsLike", feelsLike.toFloat())
        editor.apply()
    }

    // Method to save city data in shared preferences
    private fun saveCityData(city: String) {
        val sharedPreferences = getSharedPreferences("CityData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("city", city)
        editor.apply()
    }

    // Method to proceed to the main menu activity
    private fun proceedToMainMenu() {
        if (weatherDataFetched && cityDataFetched) {
            Handler(Looper.getMainLooper()).post {
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            }
        }
    }
}

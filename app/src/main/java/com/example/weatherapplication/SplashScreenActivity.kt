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

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequested = false
    private var weatherDataFetched = false
    private var cityDataFetched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Inicializa fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicia la inicialización y la carga de datos
        initializeAndLoadData()
    }

    private fun initializeAndLoadData() {
        CityRepository.init(this)

        // Solo solicitar la ubicación una vez
        if (!locationRequested) {
            requestLocation()
            locationRequested = true
        }
    }

    private fun requestLocation() {
        if (checkLocationPermission()) {
            Log.d("Location", "Permissions granted, requesting location updates")
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, OnSuccessListener { location ->
                    if (location != null) {
                        Log.d("Location", "Location received: ${location.latitude}, ${location.longitude}")
                        fetchWeatherAndCity(location.latitude, location.longitude)
                    } else {
                        Log.e("Location", "Location is null")
                        proceedToMainMenu()
                    }
                })
                .addOnFailureListener {
                    Log.e("Location", "Failed to get location: ${it.message}")
                    proceedToMainMenu()
                }
        } else {
            Log.d("Location", "Permissions not granted")
            proceedToMainMenu()
        }
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Permissions granted")
                requestLocation()
            } else {
                Log.d("Permissions", "Permissions denied")
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show()
                proceedToMainMenu()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun fetchWeatherAndCity(latitude: Double, longitude: Double) {
        if (isNetworkAvailable()) {
            obtenerDatosDesdeAPI(latitude, longitude)
            fetchCityName(latitude, longitude)
        } else {
            Log.e("Network", "No network available")
            Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
            proceedToMainMenu()
        }
    }

    private fun obtenerDatosDesdeAPI(latitude: Double, longitude: Double) {
        val apiKey = "THKTHG25EQ4QFULENB9VYT3D8"
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$latitude,$longitude?unitGroup=metric&key=$apiKey"

        Log.d("WeatherAPI", "URL: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("WeatherAPI", "Request failed: ${e.message}")
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

                        // Save the data in shared preferences or a global variable
                        saveWeatherData(temperature, description, icon)

                        weatherDataFetched = true
                        proceedToMainMenu()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("WeatherAPI", "JSON Parsing error: ${e.message}")
                        proceedToMainMenu()
                    }
                } else {
                    Log.e("WeatherAPI", "Response not successful: ${response.code}")
                    proceedToMainMenu()
                }
            }
        })
    }

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

                        // Save the city in shared preferences or a global variable
                        saveCityData(city)

                        cityDataFetched = true
                        proceedToMainMenu()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e("CityAPI", "JSON Parsing error: ${e.message}")
                        proceedToMainMenu()
                    }
                } else {
                    Log.e("CityAPI", "Response not successful: ${response.code}")
                    proceedToMainMenu()
                }
            }
        })
    }

    private fun saveWeatherData(temperature: Double, description: String, icon: String) {
        val sharedPreferences = getSharedPreferences("WeatherData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("temperature", temperature.toFloat())
        editor.putString("description", description)
        editor.putString("icon", icon)
        editor.apply()
    }

    private fun saveCityData(city: String) {
        val sharedPreferences = getSharedPreferences("CityData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("city", city)
        editor.apply()
    }

    private fun proceedToMainMenu() {
        if (weatherDataFetched && cityDataFetched) {
            Handler(Looper.getMainLooper()).post {
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            }
        }
    }
}

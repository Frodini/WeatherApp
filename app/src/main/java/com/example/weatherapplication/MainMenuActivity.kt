package com.example.weatherapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
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

data class City(val name: String, val latitude: Double, val longitude: Double) {

    fun prepareRequest() : String {
        return "http://weatherapi.com/?lat=${latitude}&lon=${longitude}"
    }
}

const val PERMISSION_REQUEST_CODE = 120

class MainMenuActivity : AppCompatActivity() {

    private val view by lazy {
        ActivityMainMenuBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestLocation()

        val btnOpenFavoriteCitiesListActivity = findViewById<Button>(R.id.btnFavoriteCities)
        btnOpenFavoriteCitiesListActivity.setOnClickListener {
            // Crear un Intent para abrir Favorite Cities
            val intent = Intent(this, FavoriteCitiesListActivity::class.java)
            startActivity(intent)
        }

        val btnOpenGeneralCitiesListActivity = findViewById<Button>(R.id.btnGeneralCities)
        btnOpenGeneralCitiesListActivity.setOnClickListener {
            val intent = Intent(this, CitiesListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestLocation() {
        val service = getSystemService(LOCATION_SERVICE) as LocationManager
        val listener = LocationListener { location ->
            // Actualiza la ubicaci처n en la UI
            val lat = location.latitude
            val lon = location.longitude
            //view.textViewLocation.text = "Latitude: $lat - Longitude: $lon"

            // Llama a la funci처n para obtener datos del clima usando las coordenadas
            obtenerDatosDesdeAPI(lat, lon)
            fetchCityName(lat,lon)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        } else {
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode != PERMISSION_REQUEST_CODE)
            return
        if(grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            requestLocation()
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
                    val jsonObject = JSONObject(responseBody)
                    val daysArray = jsonObject.getJSONArray("days")
                    if (daysArray.length() > 0) {
                        val firstDay = daysArray.getJSONObject(0)
                        val temperatura = firstDay.getDouble("temp")
                        val descripcion = jsonObject.getString("description")
                        //val wind = jsonObject.getString("windspeed")
                        //val direccion = jsonObject.getString("address")

                        runOnUiThread {
                            //findViewById<TextView>(R.id.textViewLocation).text="${direccion}"
                            findViewById<TextView>(R.id.textViewTemperature)?.text = "${temperatura}ºC"
                            //findViewById<TextView>(R.id.textViewWindDetails)?.text = "${wind}"
                            findViewById<TextView>(R.id.textViewWeatherDescription).text = "Descripción: $descripcion"
                            //findViewById<TextView>(R.id.textViewLocation).text = "Latitud: $latitude \nLongitud: $longitude  "
                        }
                    } else {
                        Log.e("JSON Parsing", "No days data available")
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
                response.use { resp -> // This ensures the response is closed properly after being used
                    val responseBody = resp.body?.string() // Read the response body string once
                    println("Response from API: $responseBody") // Debug print to verify the response content

                    try {
                        val jsonObject = JSONObject(responseBody) // Attempt to create a JSONObject
                        // Continue processing your JSON object here
                        val resultsArray = jsonObject.getJSONArray("results")
                        if (resultsArray.length() > 0) {
                            val firstResult = resultsArray.getJSONObject(0)
                            val city = firstResult.getJSONObject("components").getString("city")
                            runOnUiThread {
                                findViewById<TextView>(R.id.textViewLocation).text = city
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace() // Print the stack trace if parsing fails
                    }
                }
            }
        })
    }
}

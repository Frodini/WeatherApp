package com.example.weatherapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object (DAO) for interacting with the WeatherData table in the Room database
@Dao
interface WeatherDataDao {

    // Insert a WeatherData record into the database
    // If there is a conflict (e.g., a record with the same primary key already exists), replace the existing record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: WeatherData)

    // Query to retrieve all WeatherData records for a specific city
    // Results are ordered by reportDate and reportTime in ascending order
    @Query("SELECT * FROM weather_data WHERE cityName = :cityName ORDER BY reportDate ASC, reportTime ASC")
    suspend fun getWeatherDataByCity(cityName: String): List<WeatherData>
}

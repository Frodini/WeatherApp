package com.example.weatherapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: WeatherData): Long
    @Query("SELECT * FROM weather_data WHERE cityName = :cityName")
    suspend fun getWeatherDataByCity(cityName: String): List<WeatherData>

}

package com.example.weatherapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherData::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration() // Permitir migraciones destructivas
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

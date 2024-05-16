package com.example.weatherapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WeatherData::class], version = 2)
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
                    .addMigrations(MIGRATION_1_2) // Add migration strategy here
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define migration strategy
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE WeatherData ADD COLUMN humidity REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE WeatherData ADD COLUMN precipProbability REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE WeatherData ADD COLUMN cloudCover REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE WeatherData ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

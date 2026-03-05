package com.example.uffiziopracticaltask.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uffiziopracticaltask.room.dao.WeatherDao
import com.example.uffiziopracticaltask.room.entity.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {

        fun getDatabase(context: Context): WeatherDatabase {

            return Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "weather_db"
            ).build()
        }
    }
}
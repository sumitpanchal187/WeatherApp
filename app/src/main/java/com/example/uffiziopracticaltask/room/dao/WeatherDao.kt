package com.example.uffiziopracticaltask.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uffiziopracticaltask.room.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(list: List<WeatherEntity>)

    @Query("SELECT * FROM weather")
    suspend fun getWeather(): List<WeatherEntity>

    @Query("DELETE FROM weather")
    suspend fun clear()
}
package com.example.uffiziopracticaltask.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.uffiziopracticaltask.room.dao.WeatherDao
import com.example.uffiziopracticaltask.room.entity.WeatherEntity
import com.example.uffiziopracticaltask.room.model.Weather

class WeatherRepository(
    private val api: Weather,
    private val dao: WeatherDao
) {


    suspend fun fetchWeather(lat: Double, lon: Double) {

        val response = api.getWeather(lat, lon, "d497f49e43966aa8f37958be26e44628")

        Log.d(TAG, "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=d497f49e43966aa8f37958be26e44628&units=metric")
        val dailyForecast = mutableListOf<WeatherEntity>()
        val addedDates = mutableSetOf<String>()

        for (item in response.list) {

            val date = item.dt_txt.substring(0, 10)

            if (!addedDates.contains(date)) {

                addedDates.add(date)

                dailyForecast.add(
                    WeatherEntity(
                        date = date,
                        temperature = item.main.temp,
                        condition = item.weather[0].description,
                        icon = item.weather[0].icon
                    )
                )
            }

            if (dailyForecast.size == 3) break
        }

        dao.clear()
        dao.insertWeather(dailyForecast)

    }

    suspend fun getOfflineWeather(): List<WeatherEntity> {
        return dao.getWeather()
    }
}
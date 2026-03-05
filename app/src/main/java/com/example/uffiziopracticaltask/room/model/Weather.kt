package com.example.uffiziopracticaltask.room.model

import com.example.uffiziopracticaltask.model.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Weather {

    @GET("data/2.5/forecast")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherApiResponse
}
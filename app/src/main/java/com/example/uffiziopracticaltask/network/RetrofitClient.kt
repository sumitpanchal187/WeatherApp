package com.example.uffiziopracticaltask.network

import com.example.uffiziopracticaltask.room.model.Weather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.openweathermap.org/"

    val api: Weather by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Weather::class.java)
    }
}
package com.example.uffiziopracticaltask.model

data class WeatherApiResponse(
    val list: List<WeatherItem>
)

data class WeatherItem(
    val dt_txt: String,
    val main: MainTemp,
    val weather: List<WeatherInfo>
)

data class MainTemp(
    val temp: Double
)

data class WeatherInfo(
    val description: String,
    val icon: String
)
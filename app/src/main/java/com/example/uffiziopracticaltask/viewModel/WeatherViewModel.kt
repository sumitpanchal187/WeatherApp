package com.example.uffiziopracticaltask.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uffiziopracticaltask.repository.WeatherRepository
import com.example.uffiziopracticaltask.room.entity.WeatherEntity
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableLiveData<List<WeatherEntity>>()
    val weather: LiveData<List<WeatherEntity>> = _weather

    fun loadWeather(lat: Double, lon: Double) {

        viewModelScope.launch {

            try {

                repository.fetchWeather(lat, lon)
                _weather.value = repository.getOfflineWeather()

            } catch (_: Exception) {

                _weather.value = repository.getOfflineWeather()
            }
        }
    }
}
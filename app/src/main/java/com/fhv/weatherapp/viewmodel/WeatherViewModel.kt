package com.fhv.weatherapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.fhv.weatherapp.model.Weather
import com.fhv.weatherapp.repository.WeatherRepository

class WeatherViewModel : ViewModel() {
    fun getWeather(): LiveData<Weather> {
        return WeatherRepository.getWeather()
    }
}
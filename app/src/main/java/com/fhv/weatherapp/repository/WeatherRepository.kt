package com.fhv.weatherapp.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.fhv.weatherapp.model.Weather

object WeatherRepository {
    private val TAG = "WeatherRepository"
    private val cachedWeather: MutableLiveData<Weather> = MutableLiveData()

    fun getWeather(): LiveData<Weather> {
        return cachedWeather
    }

    fun putWeather(weather: Weather) {
        Log.d(TAG, "Updating cache...")
        Log.d(TAG, "Old: ${cachedWeather.value}")
        cachedWeather.postValue(weather)
        Log.d(TAG, "New: ${cachedWeather.value}")
    }
}
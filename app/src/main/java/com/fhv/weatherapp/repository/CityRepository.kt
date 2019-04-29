package com.fhv.weatherapp.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.fhv.weatherapp.model.City

object CityRepository {
    private val TAG = "CityRepository"
    private val cached: MutableLiveData<City> = MutableLiveData()

    fun getCity(): LiveData<City> {
        return cached
    }

    fun putCity(city: City) {
        Log.d(TAG, "Updating cache...")
        Log.d(TAG, "Old: ${cached.value}")
        cached.postValue(city)
        Log.d(TAG, "New: ${cached.value}")
    }
}
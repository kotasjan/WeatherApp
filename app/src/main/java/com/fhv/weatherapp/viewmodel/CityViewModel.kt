package com.fhv.weatherapp.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.repository.CityRepository

class CityViewModel : ViewModel() {
    fun getCity(): LiveData<City> {
        return CityRepository.getCity()
    }
}
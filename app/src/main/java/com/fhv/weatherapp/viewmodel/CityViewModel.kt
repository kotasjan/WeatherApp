package com.fhv.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.repository.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository

    init {
        val citiesDao = CityDatabase.getDatabase(application)!!.cityDao()
        repository = CityRepository(citiesDao)
    }

    fun insert(city: City) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(city)
    }

    fun getCities(): LiveData<List<City>>? {
        return repository.getCities()
    }
}
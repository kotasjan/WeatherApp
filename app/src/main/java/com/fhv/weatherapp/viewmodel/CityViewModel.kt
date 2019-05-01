package com.fhv.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.database.CityEntity
import com.fhv.weatherapp.repository.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository

    private val allCities: LiveData<List<CityEntity>>

    init {
        val citiesDao = CityDatabase.getDatabase(application)!!.cityDao()
        repository = CityRepository(citiesDao)
        allCities = repository.allCities
    }

    fun insert(city: CityEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(city)
    }

    fun getCities(): LiveData<List<CityEntity>>? {
        return repository.getCities()
    }
}
package com.fhv.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.repository.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository
    private val  cities: MutableLiveData<List<City>>

    init {
        val citiesDao = CityDatabase.getDatabase(application)!!.cityDao()
        repository = CityRepository(citiesDao)
        cities = MutableLiveData(repository.getCities())
    }

    fun insert(city: City) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(city)
        cities.postValue(repository.getCities())
    }

    fun getCities(): LiveData<List<City>>? {
        return cities
    }

}
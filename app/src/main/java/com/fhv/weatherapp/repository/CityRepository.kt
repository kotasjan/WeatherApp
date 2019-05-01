package com.fhv.weatherapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.fhv.weatherapp.database.CityDao
import com.fhv.weatherapp.database.CityEntity

class CityRepository(private val cityDao: CityDao) {
    val allCities: LiveData<List<CityEntity>> = cityDao.getCities()

    @WorkerThread
    fun insert(city: CityEntity) {
        cityDao.insertCity(city)
    }

    fun getCities(): LiveData<List<CityEntity>>? {
        return allCities
    }
}
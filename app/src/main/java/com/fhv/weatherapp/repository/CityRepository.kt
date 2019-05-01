package com.fhv.weatherapp.repository

import android.os.AsyncTask
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fhv.weatherapp.database.CityDao
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.database.CityEntity
import com.fhv.weatherapp.model.City

class CityRepository(private val cityDao: CityDao) {
    val allCities: LiveData<List<CityEntity>> = cityDao.getCities()

    @WorkerThread
    fun insert(city: City) {
        insertAsyncTask(cityDao).execute(CityEntity(city))
    }

    fun getCities(): LiveData<List<City>> {
        if (allCities.value == null){
            return MutableLiveData<List<City>>(listOf())
        }
        return Transformations.map(allCities) { allCities -> allCities.map { cityEntity -> cityEntity.city } }
    }

    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: CityDao) : AsyncTask<CityEntity, Void, Void>() {

        override fun doInBackground(vararg params: CityEntity): Void? {
            mAsyncTaskDao.insertCity(params[0])
            return null
        }
    }
}
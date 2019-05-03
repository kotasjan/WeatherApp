package com.fhv.weatherapp.repository

import android.os.AsyncTask
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fhv.weatherapp.database.CityDao
import com.fhv.weatherapp.database.CityEntity
import com.fhv.weatherapp.model.City

class CityRepository(private val cityDao: CityDao) {

    @WorkerThread
    suspend fun insert(city: City) {
        try {
            cityDao.insert(CityEntity(city.location.city, city))
        } catch (t: Exception) {
            Log.e("NEKO", t.message)
            t.printStackTrace()
        }
    }

    fun getCities(): LiveData<List<City>> {
        return asyncTask(cityDao).execute().get()
    }

    private class asyncTask internal constructor(private val cityDao: CityDao) : AsyncTask<Void, Void, LiveData<List<City>>>() {

        override fun doInBackground(vararg params: Void): LiveData<List<City>>? {
            if (cityDao.getCities().isEmpty()){ //fixme
                return MutableLiveData<List<City>>(listOf())
            }
            val ce = cityDao.getCities()
            val c = ce.map { c -> c.city }
            return MutableLiveData<List<City>>(c)
        }
    }
}
package com.fhv.weatherapp.common

import android.content.Context
import android.content.SharedPreferences
import com.fhv.weatherapp.model.City
import com.google.gson.Gson

object SharedPrefs {

    private const val PREFS_FILE = "com.fhv.weatherapp.mPrefs"
    private const val LAST_CITY = "lastCity"
    private const val CITY_LIST = "cityList"

    private var prefs: SharedPreferences? = null

    // load content of shared preferences to variable
    fun initializeSharedPreferences(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE, 0)
        loadLastCityIndex()
        loadCityList()
    }

    // load index of the last displayed city
    private fun loadLastCityIndex() {
        if (prefs != null
                && prefs!!.contains(LAST_CITY)
                && prefs!!.getString(LAST_CITY, null) != null) {
            Common.lastCityIndex = prefs?.getString(LAST_CITY, "0")!!.toInt()
        }
    }

    // save index of the last displayed city to shared preferences
    fun saveLastCityIndex() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            editor.putString(LAST_CITY, Common.lastCityIndex.toString())
            editor.apply()
        }
    }

    // load list of the cities from shared preferences
    private fun loadCityList() {
        if (Common.cityList.isEmpty()) {
            if (prefs != null
                    && prefs!!.contains(CITY_LIST)
                    && prefs!!.getString(CITY_LIST, null) != null) {

                val mJson = prefs!!.getString(CITY_LIST, null)
                Common.cityList = Gson().fromJson<MutableList<City>>(mJson, MutableList::class.java)
            }
        }
    }

    // save current list of cities to shared prefs
    fun saveCityList() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            val json = Gson().toJson(Common.cityList)
            editor.putString(CITY_LIST, json)
            editor.apply()
        }
    }
}
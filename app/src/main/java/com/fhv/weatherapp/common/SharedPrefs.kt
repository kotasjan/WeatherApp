package com.fhv.weatherapp.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
            Log.d(Common.APP_NAME, "Loaded lastCityIndex: ${Common.lastCityIndex}")
        }
    }

    // save index of the last displayed city to shared preferences
    fun saveLastCityIndex() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            editor.putString(LAST_CITY, Common.lastCityIndex.toString())
            editor.apply()
            Log.d(Common.APP_NAME, "LastCityIndex saved")
        }
    }

    // load list of the cities from shared preferences
    private fun loadCityList() {
        if (Common.cityList.isEmpty()) {
            if (prefs != null
                    && prefs!!.contains(CITY_LIST)
                    && prefs!!.getString(CITY_LIST, null) != null) {

                val mJson = prefs!!.getString(CITY_LIST, null)
                Log.d(Common.APP_NAME, "Loaded cityList: $mJson")
                Common.cityList = Gson().fromJson<MutableList<City>>(mJson, MutableList::class.java) // FIXME JAN
            }
        }
    }

    // save current list of cities to shared prefs
    fun saveCityList() {
        if (prefs != null) {
            val editor = prefs!!.edit()
            val mJson = Gson().toJson(Common.cityList)
            editor.putString(CITY_LIST, mJson)
            editor.apply()
            Log.d(Common.APP_NAME, "Saved cityList: $mJson")
        }
    }
}
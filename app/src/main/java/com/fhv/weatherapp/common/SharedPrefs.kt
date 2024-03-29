package com.fhv.weatherapp.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.fhv.weatherapp.model.City
import com.google.gson.Gson

object SharedPrefs {

    private const val PREFS_FILE = "com.fhv.weatherapp.mPrefs"
    private const val LAST_CITY = "lastCity"

    private var prefs: SharedPreferences? = null

    // load content of shared preferences to variable
    fun initializeSharedPreferences(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE, 0)
        loadLastCityIndex()
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
}
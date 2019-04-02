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

    // list of all used cities
    internal lateinit var cityList: MutableList<City>

    // load content of shared preferences to variable
    private fun initializeSharedPreferences(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE, 0)
    }

    // get index of the last displayed city
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getLastCityIndex(context: Context): Int? {
        if (prefs == null) initializeSharedPreferences(context)

        return if (prefs != null
                && prefs!!.contains(LAST_CITY)
                && prefs!!.getString(LAST_CITY, null) != null) {
            prefs!!.getString(LAST_CITY, null).toInt()
        } else {
            null
        }
    }

    // set index value of the currently displayed city
    fun setLastCityIndex(context: Context, index: Int) {
        if (prefs == null) initializeSharedPreferences(context)
        if (prefs != null) {
            val editor = prefs!!.edit()
            editor.putString(LAST_CITY, index.toString())
            editor.apply()
        }
    }

    fun getCityList(context: Context): MutableList<City>? {
        if (prefs == null) initializeSharedPreferences(context)

        return if (prefs != null
                && prefs!!.contains(CITY_LIST)
                && prefs!!.getString(CITY_LIST, null) != null) {

            val mJson = prefs!!.getString(CITY_LIST, null)
            Gson().fromJson<MutableList<City>>(mJson, MutableList::class.java)

        } else {
            null
        }
    }

    // save current list of cities to shared prefs
    fun updateCityList(context: Context) {
        if (prefs == null) initializeSharedPreferences(context)
        if (prefs != null) {
            val editor = prefs!!.edit()
            val json = Gson().toJson(cityList)
            editor.putString(CITY_LIST, json)
            editor.apply()
        }
    }
}
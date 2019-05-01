package com.fhv.weatherapp.database

import androidx.room.TypeConverter
import com.fhv.weatherapp.model.City
import com.google.gson.Gson

class CityTypeConverter {

    @TypeConverter
    fun fromString(value: String?): City? {
        return if (value == null) null else Gson().fromJson<City>(value, City::class.java)
    }

    @TypeConverter
    fun cityToString(city: City?): String? {
        return Gson().toJson(city)
    }
}
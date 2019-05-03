package com.fhv.weatherapp.database

import androidx.room.TypeConverter
import com.fhv.weatherapp.model.City
import com.google.gson.Gson

class CityTypeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun cityFromString(value: String?): City? {
           return if (value == null) null else Gson().fromJson<City>(value, City::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun cityToString(city: City?): String? {
            return Gson().toJson(city)
        }
    }
}
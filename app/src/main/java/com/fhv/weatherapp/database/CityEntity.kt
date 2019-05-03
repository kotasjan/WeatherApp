package com.fhv.weatherapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fhv.weatherapp.model.City

@Entity(tableName = "city_table")
data class CityEntity (
    @PrimaryKey
    val id : String,
    @TypeConverters(City::class)
    val city: City
    )
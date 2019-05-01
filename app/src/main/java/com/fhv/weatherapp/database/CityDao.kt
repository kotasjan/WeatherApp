package com.fhv.weatherapp.database


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: CityEntity)

    @Delete
    fun deleteCity(city: CityEntity)

    @Query("DELETE FROM city_table")
    fun deleteAll()

    @Query("SELECT * FROM city_table")
    fun getCities(): LiveData<List<CityEntity>>
}
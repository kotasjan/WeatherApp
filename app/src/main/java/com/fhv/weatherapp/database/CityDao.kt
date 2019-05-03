package com.fhv.weatherapp.database

import androidx.room.*

@Dao
interface CityDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity)

    @Delete
    fun delete(city: CityEntity)

    @Query("DELETE FROM city_table")
    fun deleteAll()

    @Query("SELECT * FROM city_table")
    fun getCities(): List<CityEntity>
}
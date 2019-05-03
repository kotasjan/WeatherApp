package com.fhv.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CityEntity::class], version = 1)
@TypeConverters(CityTypeConverter::class)
abstract class CityDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabase(context: Context): CityDatabase? {
            if (INSTANCE == null){
                synchronized(CityDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, CityDatabase::class.java, "myDB").build()
                }
            }
            return INSTANCE
        }
    }
}
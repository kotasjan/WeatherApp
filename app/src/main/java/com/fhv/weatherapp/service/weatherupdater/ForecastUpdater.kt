package com.fhv.weatherapp.service.weatherupdater

import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object ForecastUpdater {
    private val TAG = "ForecastUpdater"

    fun startInBackground() {
        cancelAllWork()

        Log.d(TAG, "Enqueue periodic forecast update in the background")

        val inputData = Data.Builder().putBoolean("updateLocation", false).build()

        val updateRequest = PeriodicWorkRequestBuilder<ForecastUpdateWorker>(15, TimeUnit.MINUTES)
                .setInputData(inputData)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("forecastUpdate", ExistingPeriodicWorkPolicy.KEEP, updateRequest)
    }

    fun updateOnce() {
        cancelAllWork()

        Log.d(TAG, "Enqueue one-time forecast update in the background")

        val inputData = Data.Builder().putBoolean("updateLocation", true).build()

        val updateRequest = OneTimeWorkRequestBuilder<ForecastUpdateWorker>()
                .setInputData(inputData)
                .build()
        WorkManager.getInstance().enqueueUniqueWork("forecastUpdate", ExistingWorkPolicy.KEEP, updateRequest)

        startInBackground()
    }

    fun cancelAllWork() {
        Log.d(TAG, "Cancelling all work")
        WorkManager.getInstance().cancelUniqueWork("forecastUpdate")
    }

}
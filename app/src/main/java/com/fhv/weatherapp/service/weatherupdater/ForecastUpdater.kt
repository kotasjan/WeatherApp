package com.fhv.weatherapp.service.weatherupdater

import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object ForecastUpdater {
    private val TAG = "ForecastUpdater"

    fun startInBackground() {
        Log.d(TAG, "Enqueue periodic forecast update in the background")
        val updateRequest = PeriodicWorkRequestBuilder<ForecastUpdateWorker>(15, TimeUnit.MINUTES)
                // todo maybe setInputData() here
                // or constraints
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("forecastUpdate", ExistingPeriodicWorkPolicy.KEEP, updateRequest)
    }

    fun updateOnce() {
        Log.d(TAG, "Enqueue one-time forecast update in the background")
        val updateRequest = OneTimeWorkRequestBuilder<ForecastUpdateWorker>()
                // todo same as above
                .build()
        WorkManager.getInstance().enqueueUniqueWork("forecastUpdate", ExistingWorkPolicy.KEEP, updateRequest)
    }

}
package com.fhv.weatherapp.requester.service

import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object ForecastUpdater {
    private val TAG = "ForecastUpdater"

    fun startInBackground() {
        Log.d(TAG, "Enqueue periodic forecast update in the background")
        val updateRequest = PeriodicWorkRequestBuilder<ForecastUpdateWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints())
                // todo maybe setInputData() here
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("forecastUpdate", ExistingPeriodicWorkPolicy.KEEP, updateRequest)
    }

    fun updateOnce() {
        Log.d(TAG, "Enqueue one-time forecast update in the background")
        val updateRequest = OneTimeWorkRequestBuilder<ForecastUpdateWorker>()
                .setConstraints(constraints())
                // todo maybe setInputData() here
                .build()
        WorkManager.getInstance().enqueueUniqueWork("forecastUpdate", ExistingWorkPolicy.KEEP, updateRequest)
    }

    private fun constraints(): Constraints {
        return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
    }
}
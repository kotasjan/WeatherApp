package com.fhv.weatherapp.requester.service

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ForecastUpdateWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    private val TAG = "ForecastUpdateWorker"

    override fun doWork(): Result {
        Log.d(TAG, "Request update weather")
        return Result.success()
    }

}
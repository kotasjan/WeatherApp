package com.fhv.weatherapp.service.weatherupdater

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.repository.WeatherRepository
import com.fhv.weatherapp.service.location.LocationUpdater
import com.fhv.weatherapp.service.weatherupdater.web.ForecastRequestException
import com.fhv.weatherapp.service.weatherupdater.web.ForecastRequester
import com.fhv.weatherapp.service.weatherupdater.web.parseJsonToWeather

class ForecastUpdateWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    private val TAG = "ForecastUpdateWorker"
    private val forecastRequester = ForecastRequester(appContext)

    override fun doWork(): Result {
        Log.d(TAG, "Start work on update weather")
        return try {
            // Update location if necessary
            if (inputData.getBoolean("updateLocation", false) || LocationUpdater.currentLocation == null) {
                Log.d(TAG, "Requesting location update")
                LocationUpdater.requestLocation(applicationContext)

                while (LocationUpdater.currentLocation == null) {
                    Log.d(TAG, "Waiting for location update... sleep 1s")
                    SystemClock.sleep(1000)
                }
            }
            val currentLocation = LocationUpdater.currentLocation!!

            // Update weather
            val apiResponse = forecastRequester.request(currentLocation)
            Log.d(TAG, "Api response: $apiResponse")
            val weather = parseJsonToWeather(apiResponse)

            // Update city history
            val city = City(weather, currentLocation)
            Common.cityList[Common.lastCityIndex] = city

            Log.d(TAG, "Parsed to Weather: $weather")
            Log.i(TAG, "Successfully retrieved forecast")
            WeatherRepository.putWeather(weather)
            Result.success()
        } catch (e: ForecastRequestException) {
            Log.e(TAG, "Error retrieving forecast")
            Log.e(TAG, e.message, e)
            Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "General error")
            Log.e(TAG, e.message, e)
            Result.retry()
        }
    }

}
package com.fhv.weatherapp.service.weatherupdater

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.repository.WeatherRepository
import com.fhv.weatherapp.service.notification.rain.RainNotifier
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
            val apiResponse = forecastRequester.request()
            Log.d(TAG, "Api response: $apiResponse")
            val weather = parseJsonToWeather(apiResponse)

            Common.cityList[Common.lastCityIndex].weather = weather

            // TODO update data of view
            // fixme - DO NOT UPDATE VIEW DATA HERE but in mainActivity, in observed viewmodel...

            Log.d(TAG, "Parsed to Weather: $weather")
            Log.i(TAG, "Successfully retrieved forecast")
            WeatherRepository.putWeather(weather)

            Log.d(TAG, "Will send notification if necessary.")
            RainNotifier.notifyOfRainIfNecessary(applicationContext, weather)

            Result.success()
        } catch (e: ForecastRequestException) {
            Log.e(TAG, "Error retrieving forecast")
            Log.e(TAG, e.message, e)
            Result.retry()
        }
    }

}
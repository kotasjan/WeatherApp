package com.fhv.weatherapp.service.weatherupdater.web

import android.util.Log
import com.fhv.weatherapp.model.*
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.json.JSONException
import org.json.JSONObject
import java.util.*

private const val TAG = "WeatherJsonParser"

fun parseJsonToWeather(apiResponse: String): Weather {
    val forecast = JSONObject(apiResponse)
    val currentWeather = parseCurrent(forecast.getJSONObject("currently"), forecast.getJSONObject("hourly"))
    Log.v(TAG, currentWeather.toString())

    val detailedCurrentWeather = parseDetailed(forecast.getJSONObject("currently"), currentWeather)
    Log.v(TAG, detailedCurrentWeather.toString())

    val hourlyWeather = parseHourly(forecast.getJSONObject("hourly"))
    Log.v(TAG, hourlyWeather.toString())

    val dailyWeather = parseDaily(forecast.getJSONObject("daily"))
    Log.v(TAG, dailyWeather.toString())

    return Weather(currentWeather, detailedCurrentWeather, hourlyWeather, dailyWeather)
}

private fun parseCurrent(current: JSONObject, hourly: JSONObject): CurrentWeather {
    return CurrentWeather(current.getDouble("temperature"),
            current.getString("icon"),
            hourly.getString("summary"),
            current.getDouble("windSpeed"),
            current.getDouble("precipProbability"))
}

private fun parseDetailed(detailed: JSONObject, currentWeather: CurrentWeather): DetailedCurrentWeather {
    return DetailedCurrentWeather(currentWeather,
            detailed.getDouble("apparentTemperature"),
            detailed.getDouble("humidity"),
            detailed.getDouble("pressure"),
            detailed.getDouble("cloudCover"),
            detailed.getDouble("uvIndex"),
            detailed.getDouble("ozone"))
}

private fun parseHourly(hourly: JSONObject): HourlyWeather {
    val HOURS = 25
    val probabilities: MutableList<HourlyWeather.Entry> = mutableListOf()
    val temperatures: MutableList<HourlyWeather.Entry> = mutableListOf()
    try {
        val data = hourly.getJSONArray("data")
        for (i in 0 until HOURS) {
            val entry = data.getJSONObject(i)
            val timestamp = entry.getLong("time")
            val hour = LocalDateTime.fromDateFields(Date(timestamp)).hourOfDay
            val probability = entry.getDouble("precipProbability")
            val temperature = entry.getDouble("temperature")
            probabilities.add(HourlyWeather.Entry(i, hour, probability))
            temperatures.add(HourlyWeather.Entry(i, hour, temperature))
        }
    } catch (e: JSONException) {
        Log.e(TAG, e.message, e)
    }
    return HourlyWeather(probabilities, temperatures)
}

private fun parseDaily(daily: JSONObject): DailyWeather {
    val DAYS = 7
    val days: MutableList<DailyWeather.Entry> = mutableListOf()
    try {
        val data = daily.getJSONArray("data")
        for (i in 1..DAYS) {           // starting with 1 to skip today
            val entry = data.getJSONObject(i)
            val timestamp = entry.getLong("time")
            val day = LocalDate.fromDateFields(Date(timestamp * 1000)) // timestamp is in seconds but we need millis
            days.add(DailyWeather.Entry(day,
                    entry.getString("icon"),
                    entry.getDouble("temperatureLow"),
                    entry.getDouble("temperatureHigh")))
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return DailyWeather(days, daily.getString("summary"))
}


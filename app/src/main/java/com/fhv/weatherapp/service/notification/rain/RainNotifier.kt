package com.fhv.weatherapp.service.notification.rain

import android.content.Context
import android.util.Log
import com.fhv.weatherapp.model.Weather
import com.fhv.weatherapp.service.notification.sendNotification

object RainNotifier {
    private val TAG = "RainNotifier"
    private var condition: Condition = Condition(0.0)

    init {
        condition.upsertRainProbabilityThresholdIn(1, 1.0)
        condition.upsertRainProbabilityThresholdIn(2, 0.0)

    }

    fun notifyOfRainIfNecessary(context: Context, weather: Weather) {
        var rainy = false
        val notificationText = StringBuilder("I'm sure that ")

        val currentRain = weather.currentWeather.precipProbability
        Log.d(TAG, "Current rain probability: $currentRain")
        Log.d(TAG, "Current threshold: ${condition.getCurrentRainProbabilityThreshold()}")

        if (currentRain > condition.getCurrentRainProbabilityThreshold()) {
            rainy = true
            val percent = currentRain * 100
            notificationText.append("right now is going to rain ($percent%), ")
        }

        val hourlyRains = weather.hourlyWeather.precipProbabilities
        for (c in condition.getRainProbabilityThresholds().entries) {
            val hour = c.key
            val probability = hourlyRains.first { e -> e.hour == hour }.value
            val threshold = c.value

            Log.d(TAG, "$hour hour rain probability: $probability")
            Log.d(TAG, "$hour hour threshold: $threshold")

            if (probability > threshold) {
                rainy = true
                val percent = currentRain * 100
                notificationText.append("in $hour hours is going to rain ($percent%), ")
            }
        }

        if (rainy) {
            Log.i(TAG, "Sending rain notification")
            notificationText.append("so better take an umbrella.")
            sendNotification(context, "RAIN!", notificationText.toString())
        }
    }

}
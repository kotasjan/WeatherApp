package com.fhv.weatherapp.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fhv.weatherapp.R
import kotlin.random.Random


private const val CHANNEL_ID = "weather_app_channel"

fun sendNotification(context: Context, title: String, text: String) {
    val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val mBuilder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
    mBuilder.setSmallIcon(R.drawable.ic_priority_high_24px)
            .setContentTitle(title)
            .setContentText(text)
            .setChannelId(CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

    val bigTextStyle = NotificationCompat.BigTextStyle()
    bigTextStyle.setBigContentTitle(title)
    bigTextStyle.bigText(text)
    mBuilder.setStyle(bigTextStyle)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(CHANNEL_ID,
                "Weather App notification channel", NotificationManager.IMPORTANCE_HIGH)
        mNotificationManager.createNotificationChannel(channel)
    }

    mNotificationManager.notify(Random.nextInt(), mBuilder.build())
}

package com.fhv.weatherapp.service.notification

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.fhv.weatherapp.R
import kotlin.random.Random

fun sendNotification(context: Context, title: String, text: String) {
    val mBuilder = NotificationCompat.Builder(context, "IGNORED")
            .setSmallIcon(R.drawable.navigation_empty_icon) //todo
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(Random.nextInt(), mBuilder.build())
    }
}
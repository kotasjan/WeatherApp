package com.fhv.weatherapp.service.notification.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import com.fhv.weatherapp.service.notification.sendNotification


class NetworkBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "NetBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Received broadcast.")
        if (!isOnline(context!!)) {
            Log.d(TAG, "No connection - sending notification")
            sendNotification(context, "NO INTERNET", "There will be no forecast because there is no internet connection")
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }
}
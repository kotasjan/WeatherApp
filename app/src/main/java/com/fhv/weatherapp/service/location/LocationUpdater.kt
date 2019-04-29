package com.fhv.weatherapp.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.model.CurrentLocation
import java.io.IOException
import java.util.*

object LocationUpdater {
    var currentLocation: CurrentLocation? = null
    private lateinit var locationManager: LocationManager
    private val TAG = "LocationUpdater"

    @SuppressLint("MissingPermission")
    fun requestLocation(context: Context) {
        currentLocation = null
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String?) {}

                override fun onProviderDisabled(provider: String?) {}

                override fun onLocationChanged(location: Location?) {
                    if (location != null) {

                        val curloc = getLocationResult(location, context)

                        if (curloc != null) {

                            var including = false

                            for (city in Common.cityList) {
                                if (city.location.city == curloc.city) {
                                    Common.lastCityIndex = Common.cityList.indexOf(city)
                                    including = true
                                    break
                                }
                            }

                            if (!including) {
                                Common.cityList.add(City(null, curloc))
                                Common.lastCityIndex = Common.cityList.size - 1
                            }

                            currentLocation = curloc

                            locationManager.removeUpdates(this)
                        }
                    }
                }
            }, Looper.getMainLooper())

            val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (localNetworkLocation != null) {
                val location: Location = localNetworkLocation
                getLocationResult(location, context)

                val curloc = getLocationResult(location, context)

                if (curloc != null) {

                    var including = false

                    for (city in Common.cityList) {
                        if (city.location.city == curloc.city) {
                            Common.lastCityIndex = Common.cityList.indexOf(city)
                            including = true
                            break
                        }
                    }

                    if (!including) {
                        Common.cityList.add(City(null, curloc))
                        Common.lastCityIndex = Common.cityList.size - 1
                    }

                    currentLocation = curloc
                }
            }
        } else {
            throw RuntimeException("Location permission not granted.")
        }
    }

    private fun getLocationResult(location: Location, context: Context): CurrentLocation? {
        try {
            val city: String = getCityName(location.latitude, location.longitude, context)
            Log.d(TAG, city + "\n lat: " + location.latitude + "\n lng: " + location.longitude)
            return CurrentLocation(city, location.latitude, location.longitude)
        } catch (e: Exception) {
            Log.e(TAG, "City name was not found")
            Log.e(TAG, e.message, e)
        }
        return null
    }

    private fun getCityName(lat: Double, lng: Double, context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>

        try {
            addresses = geocoder.getFromLocation(lat, lng, 10)
            if (addresses.isNotEmpty()) {
                for (address in addresses) {
                    if (address.locality != null && address.locality.isNotEmpty()) {
                        return address.locality
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }

        Log.d(TAG, "City name was not found.")
        return ""
    }
}
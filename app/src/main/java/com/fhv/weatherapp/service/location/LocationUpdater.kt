package com.fhv.weatherapp.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.*
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.model.CurrentLocation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*
import androidx.lifecycle.ViewModelProviders
import com.fhv.weatherapp.MainActivity
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.repository.CityRepository
import com.fhv.weatherapp.viewmodel.CityViewModel

object LocationUpdater {
    var currentLocation: CurrentLocation? = null
    private lateinit var locationManager: LocationManager
    private const val TAG = "LocationUpdater"

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
                        processLocationResult(location, context)
                        locationManager.removeUpdates(this)
                    }
                }
            }, Looper.getMainLooper())

            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                processLocationResult(location, context)
            }
        } else {
            askForEnableLocation(context)
        }
    }

    private fun processLocationResult(location: Location, context: Context) {

        val curLoc = getLocationResult(location, context)

        if (curLoc != null) {

            var including = false
            val cities = CityRepository(CityDatabase.getDatabase(MainActivity.getActivity())!!.cityDao()).getCities()

            for (city in cities.listIterator()) {
                if (city.location.city == curLoc.city) {
                    Common.lastCityIndex = cities.indexOf(city)
                    including = true
                    break
                }
            }

            if (!including) {

                ViewModelProviders.of(MainActivity.getActivity())
                        .get(CityViewModel::class.java)
                        .insert(City(null, curLoc))

                val gotCityList: List<City> = ViewModelProviders.of(MainActivity.getActivity()).get(CityViewModel::class.java).getCities()?.value!!
                if(gotCityList.isNotEmpty()) Common.lastCityIndex = gotCityList.size - 1
            }

            currentLocation = curLoc
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

    private fun askForEnableLocation(context: Context) {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(LocationRequest())
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { requestLocation(context) }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(MainActivity.getActivity(),
                            Common.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}

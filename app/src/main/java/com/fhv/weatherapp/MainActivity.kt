package com.fhv.weatherapp

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.*
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.model.CurrentLocation
import com.fhv.weatherapp.model.SmallWeather
import com.fhv.weatherapp.service.weatherupdater.ForecastUpdater
import com.fhv.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var listView: ListView? = null
    private val cityHeaderName: TextView? = null

    private lateinit var dataModels: ArrayList<SmallWeather>
    private var adapter: CustomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ForecastUpdater.startInBackground()

        /*button.setOnClickListener { ForecastUpdater.updateOnce() }

        ViewModelProviders.of(this)
                .get(WeatherViewModel::class.java)
                .getWeather()
                .observe(this, Observer { weather ->
                    info.text = weather.toString()
                })

        // setting listener for get location button
        btn_get_location.setOnClickListener { getLocationListener() }*/

        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mDrawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = setupDrawerToggle()
        mDrawer!!.addDrawerListener(drawerToggle)



        listView = findViewById(R.id.list) as ListView
        dataModels = ArrayList<SmallWeather>()
        dataModels.add(SmallWeather("Dornbirn", "26", "snow"))
        dataModels.add(SmallWeather("Dornbirn", "25", "snow"))
        dataModels.add(SmallWeather("Dornbirn", "23", "snow"))
        dataModels.add(SmallWeather("Dornbirn", "21", "snow"))
        adapter = CustomAdapter(dataModels, applicationContext)
        listView!!.setAdapter(adapter)
    }


    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle!!.onConfigurationChanged(newConfig)
    }


































    // check if user allowed to use location services
    private fun getLocationListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), Common.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION)
        } else {
            getLocation()
        }
    }

    // check result of request for permission to use network location services
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Common.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // adding location update listeners and get location data
    @SuppressLint("MissingPermission")
    private fun getLocation() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String?) {}

                override fun onProviderDisabled(provider: String?) {}

                override fun onLocationChanged(location: Location?) {
                    if (location != null) {
                        getLocationResult(location)
                        locationManager.removeUpdates(this)
                    }
                }
            })

            val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (localNetworkLocation != null) {
                val location: Location = localNetworkLocation
                getLocationResult(location)
            }
        } else {

            val builder = LocationSettingsRequest.Builder().addLocationRequest(LocationRequest())
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener { getLocationListener() }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this@MainActivity,
                                Common.REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    // print data as Toast and return CurrentLocation data object
    private fun getLocationResult(location: Location): CurrentLocation? {
        try {
            val city: String = getCityName(location.latitude, location.longitude)
            Toast.makeText(this, city + "\n lat: " + location.latitude +
                    "\n lng: " + location.longitude, Toast.LENGTH_SHORT).show()
            return CurrentLocation(city, location.latitude, location.longitude)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "City name was not found", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    // get city name from coordinates
    private fun getCityName(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
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
            e.printStackTrace()
        }

        Log.d(Common.APP_NAME, "City name was not found.")

        return ""
    }
}

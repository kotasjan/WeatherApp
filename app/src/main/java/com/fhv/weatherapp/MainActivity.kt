package com.fhv.weatherapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.fhv.weatherapp.adapters.HeaderListAdapter
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.common.SharedPrefs
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.database.CityEntity
import com.fhv.weatherapp.service.notification.network.NetworkBroadcastReceiver
import com.fhv.weatherapp.service.weatherupdater.ForecastUpdater
import com.fhv.weatherapp.viewmodel.CityViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.list_view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var listView: ListView? = null

    private var adapter: HeaderListAdapter? = null

    private val broadcastReceiver: BroadcastReceiver = NetworkBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // notify on no internet connection
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(broadcastReceiver, filter)

        // This has to be called always in first/main activity to load previously saved state
        SharedPrefs.initializeSharedPreferences(this)

        if (Common.cityList.isEmpty()) {
            askForPermissionsAndUpdateWeather()
        } else {
            ForecastUpdater.startInBackground()
        }

        /* button.setOnClickListener { ForecastUpdater.updateOnce() } */
        /* setting listener for get location button
        btn_get_location.setOnClickListener { getLocationListener() }*/

        //header filled with mock data
        navigationView = findViewById(R.id.nvView)
        val headerLayout = navigationView!!.getHeaderView(0)
        val cityText = headerLayout.findViewById(R.id.name_of_the_city) as TextView
        cityText.text = "Current location"
        val temperatureText = headerLayout.findViewById(R.id.temperature_header) as TextView
        temperatureText.text = "24"
        val iconWeather = headerLayout.findViewById(R.id.icon_header) as WebView
        prepareIcon(iconWeather, "fog", "medium")


        //first card view
        val temperatureMainView = findViewById<TextView>(R.id.temperature_main_view)
        val iconMainView = findViewById<WebView>(R.id.icon_main_view)
        val summaryMainView = findViewById<TextView>(R.id.summary_main_view)
        val summaryMainView2 = findViewById<TextView>(R.id.summary_main_view2)
        val iconWindy = findViewById<WebView>(R.id.windy_icon)
        val iconRainy = findViewById<WebView>(R.id.rainy_icon)
        val windSpeed = findViewById<TextView>(R.id.wind_speed)
        val rainProp = findViewById<TextView>(R.id.rain_prop)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)




        ViewModelProviders.of(this)
                .get(CityViewModel::class.java)
                .getCities()?.observe(this, androidx.lifecycle.Observer<List<CityEntity>>  { cityList ->
                    temperatureMainView.text = Math.round(cityList[Common.lastCityIndex].city.weather!!.currentWeather.temperature).toString() + " \u2103"
                    prepareIcon(iconMainView, cityList[Common.lastCityIndex].city.weather!!.currentWeather.icon, "large")
                    summaryMainView.text = cityList[Common.lastCityIndex].city.weather!!.currentWeather.summary
                    summaryMainView2.text = cityList[Common.lastCityIndex].city.weather!!.currentWeather.summary
                    prepareIcon(iconWindy, "wind", "tiny")
                    prepareIcon(iconRainy, "rain", "tiny")
                    windSpeed.text = cityList[Common.lastCityIndex].city.weather!!.currentWeather.windSpeed.toString() + " m/s"
                    rainProp.text = (cityList[Common.lastCityIndex].city.weather!!.currentWeather.precipProbability * 100).toInt().toString() + "%"
                    toolbarTitle.text = cityList[Common.lastCityIndex].city.location.city
                })

        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 9000L

        valueAnimator.addUpdateListener{
            var progress =  it.animatedValue as Float
            var width = summaryMainView.width
            var translationX = width * progress
            summaryMainView.translationX = -translationX
            summaryMainView2.translationX = -(translationX - width)

        }
        valueAnimator.start()


        val weatherCard = findViewById<CardView>(R.id.weather_card)
        weatherCard.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, WeatherDetails::class.java)
                startActivity(intent)
            }
        })


        // setting listener for get location button
        getWeatherButton.setOnClickListener { askForPermissionsAndUpdateWeather() }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mDrawer = findViewById(R.id.drawer_layout)
        drawerToggle = setupDrawerToggle()
        mDrawer!!.addDrawerListener(drawerToggle)

        listView = findViewById(R.id.list)
        var cityList = CityDatabase.getDatabase(application)!!.cityDao().getCities().value?.map{ cityEntity -> cityEntity.city }
        adapter = HeaderListAdapter(ArrayList(cityList), applicationContext)
        listView!!.adapter = adapter
    }

    // This method is called always before activity ends (usually to save activity state)
    override fun onStop() {
        
        SharedPrefs.saveLastCityIndex()

        Log.d(Common.APP_NAME, "onStop")

        super.onStop()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun prepareIcon(icon: WebView, weatherIconType: String, iconSize: String) {
        var iconSizeString = String.format("file:///android_asset/%sWeatherImage.html", iconSize)
        icon.settings.javaScriptEnabled = true
        icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null)  //disabled hardware acceleration.. strangely, it significantly improves performance
        icon.loadUrl(iconSizeString)
        icon.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:set_icon_type('$weatherIconType')")
            }
        }
    }

    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }


    // check if user allowed to use location services
    private fun askForPermissionsAndUpdateWeather() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), Common.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION)
        } else {
            ForecastUpdater.updateOnce()
        }
    }

    // check result of request for permission to use network location services
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Common.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ForecastUpdater.updateOnce()
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

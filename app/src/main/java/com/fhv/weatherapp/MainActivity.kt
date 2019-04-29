package com.fhv.weatherapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.common.SharedPrefs
import com.fhv.weatherapp.service.notification.network.NetworkBroadcastReceiver
import com.fhv.weatherapp.service.weatherupdater.ForecastUpdater
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

        //header filled with mock data
        navigationView = findViewById(R.id.nvView) as NavigationView
        val headerLayout = navigationView!!.getHeaderView(0)
        val cityText = headerLayout.findViewById(R.id.name_of_the_city) as TextView
        cityText.setText(R.string.app_name)
        val temperatureText = headerLayout.findViewById(R.id.temperature_header) as TextView
        temperatureText.setText("24")
        val iconWeather = headerLayout.findViewById(R.id.icon_header) as WebView
        prepareIcon(iconWeather, "fog", "medium")



        //first card view

        val temperatrueMainView = findViewById(R.id.temperature_main_view) as TextView
        temperatrueMainView.setText("29 C")
        val iconMainView = findViewById(R.id.icon_main_view) as WebView
        prepareIcon(iconMainView, "fog", "large")
        val summaryMainView = findViewById(R.id.summary_main_view) as TextView
        summaryMainView.setText("Rain starting later this afternoon, continuing until this evening.")
        val summaryMainView2 = findViewById(R.id.summary_main_view2) as TextView
        summaryMainView2.setText("Rain starting later this afternoon, continuing until this evening.")


        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 9000L

        valueAnimator.addUpdateListener{
            var progress =  it.animatedValue as Float
            var width = summaryMainView.getWidth()
            var translationX = width * progress
            summaryMainView.setTranslationX(-translationX)
            summaryMainView2.setTranslationX(-(translationX - width))

        }
        valueAnimator.start();

        val iconWindy = findViewById(R.id.windy_icon) as WebView
        val iconRainy = findViewById(R.id.rainy_icon) as WebView
        prepareIcon(iconWindy, "wind", "tiny")
        prepareIcon(iconRainy, "rain", "tiny")

        /* button.setOnClickListener { ForecastUpdater.updateOnce() } */

      /*  ViewModelProviders.of(this)
                .get(WeatherViewModel::class.java)
                .getWeather()
                .observe(this, Observer { weather ->
                    info.text = weather.toString()
                })*/

        // setting listener for get location button
        getWeatherButton.setOnClickListener { askForPermissionsAndUpdateWeather() }

        toolbar = findViewById(R.id.toolbar) as Toolbar
        val toolbarTitle = findViewById(R.id.toolbar_title) as TextView
        setSupportActionBar(toolbar)
        toolbarTitle.setText("Current location")
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)


        mDrawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = setupDrawerToggle()
        mDrawer!!.addDrawerListener(drawerToggle)


        //list filled with mock data
        listView = findViewById(R.id.list) as ListView
        adapter = HeaderListAdapter(ArrayList(Common.cityList), applicationContext)
        listView!!.setAdapter(adapter)


    }

    // This method is called always before activity ends (usually to save activity state)
    override fun onStop() {

        SharedPrefs.saveCityList()
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

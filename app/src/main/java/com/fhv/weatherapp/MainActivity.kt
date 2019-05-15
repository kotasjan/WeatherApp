package com.fhv.weatherapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fhv.weatherapp.adapters.DailyWeatherListAdapter
import com.fhv.weatherapp.adapters.HeaderListAdapter
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.common.SharedPrefs
import com.fhv.weatherapp.database.CityDatabase
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.model.DailyWeather
import com.fhv.weatherapp.repository.CityRepository
import com.fhv.weatherapp.service.notification.network.NetworkBroadcastReceiver
import com.fhv.weatherapp.service.weatherupdater.ForecastUpdater
import com.fhv.weatherapp.view.graph.ForecastGraphHandler
import com.fhv.weatherapp.viewmodel.CityViewModel
import com.github.mikephil.charting.charts.BarChart
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.list_view.*

class MainActivity : AppCompatActivity() {

    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navigationView: NavigationView? = null
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var listView: ListView? = null
    private var adapter: HeaderListAdapter? = null
    private val broadcastReceiver: BroadcastReceiver = NetworkBroadcastReceiver()


    private lateinit var repository: CityRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = CityRepository(CityDatabase.getDatabase(application)!!.cityDao())
        activity = this

        // notify on no internet connection
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(broadcastReceiver, filter)

        // This has to be called always in first/main activity to load previously saved state
        SharedPrefs.initializeSharedPreferences(this)

        if (repository.getCities().isNullOrEmpty()) {
            askForPermissionsAndUpdateWeather()
        } else {
            ForecastUpdater.startInBackground()
            showLoadingSnackbar()
        }

        /* button.setOnClickListener { ForecastUpdater.updateOnce() } */
        /* setting listener for get location button
        btn_get_location.setOnClickListener { getLocationListener() }*/

        //header menu view
        navigationView = findViewById(R.id.nvView) as NavigationView
        val headerLayout = navigationView!!.getHeaderView(0)
        val cityText = headerLayout.findViewById(R.id.name_of_the_city) as TextView
        val temperatureText = headerLayout.findViewById(R.id.temperature_header) as TextView
        val iconWeather = headerLayout.findViewById(R.id.icon_header) as WebView
        iconWeather.setBackgroundColor(0)


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

        // second card - hourly graphs
        val graph = findViewById<BarChart>(R.id.graph)
        val changeTypeTextView = findViewById<TextView>(R.id.change_graph_type_tv)
        val changeGraphTypeButton = findViewById<ImageButton>(R.id.button_graph)
        val graphHandler = ForecastGraphHandler(graph)
        changeGraphTypeButton.setOnClickListener { view ->
            graphHandler.changeGraphType()
            changeTypeTextView.text = graphHandler.graphStateAsString
        }

        //third card view
        var listDailyView = findViewById<RecyclerView>(R.id.recycler_view)
        val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        listDailyView!!.setLayoutManager(manager)



        ViewModelProviders.of(this)
                .get(CityViewModel::class.java)
                .getCities()?.observe(this, androidx.lifecycle.Observer<List<City>> { cityList ->
                    if (cityList.isNullOrEmpty() || cityList.getOrNull(Common.lastCityIndex)?.weather == null) return@Observer

                    temperatureMainView.text = Math.round(cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.temperature!!).toString() + getResources().getString(R.string.degree_celcius)
                    prepareIcon(iconMainView, cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.icon!!, "large")
                    summaryMainView.text = cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.summary!!
                    summaryMainView2.text = cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.summary!!
                    prepareIcon(iconWindy, "wind", "tiny")
                    prepareIcon(iconRainy, "rain", "tiny")
                    windSpeed.text = cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.windSpeed!!.toString() + getResources().getString(R.string.wind_speed)
                    rainProp.text = (cityList.getOrNull(Common.lastCityIndex)?.weather?.currentWeather?.precipProbability!! * 100).toInt().toString() + getResources().getString(R.string.percentage)
                    toolbarTitle.text = cityList.getOrNull(Common.lastCityIndex)?.location?.city!!
                    cityText.setText(cityList.getOrNull(Common.lastCityIndex)?.location!!.city!!)
                    temperatureText.setText(Math.round(cityList.getOrNull(Common.lastCityIndex)?.weather!!.currentWeather.temperature).toString() + getResources().getString(R.string.degree_celcius))
                    prepareIcon(iconWeather, cityList.getOrNull(Common.lastCityIndex)?.weather!!.currentWeather.icon, "medium")
                    var dailyAdapter = DailyWeatherListAdapter(cityList.getOrNull(Common.lastCityIndex)?.weather!!.dailyWeather.days, applicationContext)
                    listDailyView!!.setAdapter(dailyAdapter)
                    // update graphs
                    graphHandler.updateData(cityList.getOrNull(Common.lastCityIndex)?.weather!!.hourlyWeather)
                })

        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 9000L

        valueAnimator.addUpdateListener {
            var progress = it.animatedValue as Float
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

        //header menu view
        listView = findViewById(R.id.list)
        val allCities = repository.getCities()
        adapter = HeaderListAdapter(ArrayList(allCities), applicationContext)
        listView!!.adapter = adapter


    }

    // This method is called always before activity ends (usually to save activity state)
    override fun onStop() {

        SharedPrefs.saveLastCityIndex()
        Log.d(Common.APP_NAME, "onStop")

        super.onStop()
    }

    override fun onDestroy() {

        SharedPrefs.saveLastCityIndex()
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun prepareIcon(icon: WebView, weatherIconType: String, iconSize: String) {
        var iconSizeString = String.format("file:///android_asset/%sWeatherImage.html", iconSize)
        icon.settings.javaScriptEnabled = true
        icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
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
            showLoadingSnackbar()
        }
    }

    // check result of request for permission to use network location services
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Common.PERMISSION_REQUEST_ACCESS_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ForecastUpdater.updateOnce()
                    showLoadingSnackbar()

                } else {
                    Toast.makeText(this, getString(R.string.perrmision_not_granted), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoadingSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.loading), Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("OK") { }
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
    }


    companion object ActivityHolder {
        private var activity: AppCompatActivity? = null
        @SuppressLint("StaticFieldLeak")
        private lateinit var snackbar: Snackbar

        fun getActivity(): AppCompatActivity {
            return activity!!
        }

        fun hideLoadingSnackbar() {
            snackbar.dismiss()
        }
    }
}

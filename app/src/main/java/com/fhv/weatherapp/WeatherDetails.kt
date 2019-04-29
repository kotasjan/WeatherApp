package com.fhv.weatherapp

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import com.fhv.weatherapp.adapters.DetailsListAdapter
import com.fhv.weatherapp.model.Details
import com.fhv.weatherapp.viewmodel.CityViewModel
import java.util.*

class WeatherDetails : FragmentActivity() {

    private var adapter: DetailsListAdapter? = null
    private var listView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        val toolbarTitle = findViewById(R.id.toolbar_title) as TextView
        val temperatureDetails = findViewById<TextView>(R.id.details_temperature)
        val iconDetails = findViewById<WebView>(R.id.details_icon)
        val summaryDetail = findViewById<TextView>(R.id.detail_summary)
        listView = findViewById(R.id.list_details) as ListView
        var dataModels = ArrayList<Details>()

        ViewModelProviders.of(this)
                .get(CityViewModel::class.java)
                .getCity()
                .observe(this, android.arch.lifecycle.Observer { city ->
                    toolbarTitle.text = city!!.location.city
                    temperatureDetails.text = Math.round(city!!.weather!!.currentWeather.temperature).toString() + " \u2103"
                    summaryDetail.text = city!!.weather!!.currentWeather.summary
                    prepareIcon(iconDetails, city!!.weather!!.currentWeather.icon)
                    dataModels.add(Details("Apparental temp.", Math.round(city!!.weather!!.currentWeather.temperature).toString() + " \u2103"))
                    dataModels.add(Details("Probability of rain", (city!!.weather!!.currentWeather.precipProbability * 100).toInt().toString() + "%"))
                    dataModels.add(Details("Humidity", (city!!.weather!!.detailedCurrentWeather.humidity * 100).toInt().toString() + "%"))
                    dataModels.add(Details("Pressure", Math.round(city!!.weather!!.detailedCurrentWeather.pressure).toString() + " Pa"))
                    dataModels.add(Details("Wind speed", city!!.weather!!.currentWeather.windSpeed.toString() + " m/s"))
                    dataModels.add(Details("Cloud cover", (city!!.weather!!.detailedCurrentWeather.cloudCover * 100).toInt().toString() + "%"))
                    dataModels.add(Details("UV index", city!!.weather!!.detailedCurrentWeather.uvIndex.toInt().toString()))
                    dataModels.add(Details("Ozone", Math.round(city!!.weather!!.detailedCurrentWeather.ozone).toString() + " DU"))
                    adapter = DetailsListAdapter(dataModels, applicationContext)
                    listView!!.setAdapter(adapter)
                })
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun prepareIcon(icon: WebView, weatherIconType: String) {
        icon.settings.javaScriptEnabled = true
        icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null)  //disabled hardware acceleration.. strangely, it significantly improves performance
        icon.loadUrl("file:///android_asset/detailsWeatherImage.html")
        icon.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:set_icon_type('$weatherIconType')")
            }
        }
    }

}

package com.fhv.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.fhv.weatherapp.adapters.DetailsListAdapter
import com.fhv.weatherapp.common.Common
import com.fhv.weatherapp.database.CityEntity
import com.fhv.weatherapp.model.City
import com.fhv.weatherapp.model.Details
import com.fhv.weatherapp.viewmodel.CityViewModel
import java.util.*

class WeatherDetails : FragmentActivity() {

    private var adapter: DetailsListAdapter? = null
    private var listView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        val temperatureDetails = findViewById<TextView>(R.id.details_temperature)
        val iconDetails = findViewById<WebView>(R.id.details_icon)
        val summaryDetail = findViewById<TextView>(R.id.detail_summary)
        listView = findViewById<ListView>(R.id.list_details)
        val dataModels = ArrayList<Details>()

        ViewModelProviders.of(this)
                .get(CityViewModel::class.java)
                .getCities()?.observe(this, androidx.lifecycle.Observer<List<City>> { cityList ->
                    if (cityList.isNullOrEmpty() || cityList.getOrNull(Common.lastCityIndex)?.weather==null) return@Observer

                    toolbarTitle.text = cityList[Common.lastCityIndex].location.city
                    temperatureDetails.text = Math.round(cityList[Common.lastCityIndex].weather!!.currentWeather.temperature).toString() + " \u2103"
                    summaryDetail.text = cityList[Common.lastCityIndex].weather!!.currentWeather.summary
                    prepareIcon(iconDetails, cityList[Common.lastCityIndex].weather!!.currentWeather.icon)
                    dataModels.add(Details("Apparental temp.", Math.round(cityList[Common.lastCityIndex].weather!!.currentWeather.temperature).toString() + " \u2103"))
                    dataModels.add(Details("Probability of rain", (cityList[Common.lastCityIndex].weather!!.currentWeather.precipProbability * 100).toInt().toString() + "%"))
                    dataModels.add(Details("Humidity", (cityList[Common.lastCityIndex].weather!!.detailedCurrentWeather.humidity * 100).toInt().toString() + "%"))
                    dataModels.add(Details("Pressure", Math.round(cityList[Common.lastCityIndex].weather!!.detailedCurrentWeather.pressure).toString() + " Pa"))
                    dataModels.add(Details("Wind speed", cityList[Common.lastCityIndex].weather!!.currentWeather.windSpeed.toString() + " m/s"))
                    dataModels.add(Details("Cloud cover", (cityList[Common.lastCityIndex].weather!!.detailedCurrentWeather.cloudCover * 100).toInt().toString() + "%"))
                    dataModels.add(Details("UV index", cityList[Common.lastCityIndex].weather!!.detailedCurrentWeather.uvIndex.toInt().toString()))
                    dataModels.add(Details("Ozone", Math.round(cityList[Common.lastCityIndex].weather!!.detailedCurrentWeather.ozone).toString() + " DU"))
                    adapter = DetailsListAdapter(dataModels, applicationContext)
                    listView!!.adapter = adapter
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

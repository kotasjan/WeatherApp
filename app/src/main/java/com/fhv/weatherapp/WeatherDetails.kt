package com.fhv.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.TextView
import com.fhv.weatherapp.adapters.DetailsListAdapter
import com.fhv.weatherapp.model.Details

import kotlinx.android.synthetic.main.activity_weather_details.*
import kotlinx.android.synthetic.main.row_item.*
import java.util.ArrayList

class WeatherDetails : Activity() {

    private var adapter: DetailsListAdapter? = null
    private var listView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)
        val location = intent.getStringExtra("Location")
        val toolbarTitle = findViewById(R.id.toolbar_title) as TextView
        toolbarTitle.setText(location)

        val temperatureDetails = findViewById<TextView>(R.id.details_temperature)
        val iconDetails = findViewById<WebView>(R.id.details_icon)
        val summaryDetail = findViewById<TextView>(R.id.detail_summary)
        temperatureDetails.text = "26 C"
        summaryDetail.text = "Rain starting later this afternoon, continuing until this evening."
        prepareIcon(iconDetails, "wind")



        listView = findViewById(R.id.list_details) as ListView
        var dataModels = ArrayList<Details>()
        dataModels.add(Details("Apparental temp.", "23 C"))
        dataModels.add(Details("Probability of rain", "60%"))
        dataModels.add(Details("Humidity", "25%"))
        dataModels.add(Details("Pressure", "1042hpa"))
        dataModels.add(Details("Wind speed", "10km/s"))
        dataModels.add(Details("Cloud cover", "50%"))
        dataModels.add(Details("UV index", "123"))
        dataModels.add(Details("Ozone", "321"))
        adapter = DetailsListAdapter(dataModels, applicationContext)
        listView!!.setAdapter(adapter)




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

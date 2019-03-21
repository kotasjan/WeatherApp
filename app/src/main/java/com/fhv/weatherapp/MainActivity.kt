package com.fhv.weatherapp;

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fhv.weatherapp.service.weatherupdater.ForecastUpdater
import com.fhv.weatherapp.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { view -> ForecastUpdater.updateOnce() }

        ViewModelProviders.of(this)
                .get(WeatherViewModel::class.java)
                .getWeather()
                .observe(this, Observer { weather ->
                    info.text = weather.toString()
                })
    }
}

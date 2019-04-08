package com.fhv.weatherapp.common

import com.fhv.weatherapp.model.City


object Common {

    internal const val APP_NAME = "WeatherApp"
    internal const val PERMISSION_REQUEST_ACCESS_COARSE_LOCATION = 100
    internal const val REQUEST_CHECK_SETTINGS = 0x1

    // list of all used cities
    internal var cityList: MutableList<City> = arrayListOf()

    // index of the city which was displayed the last
    internal var lastCityIndex: Int = 0

}
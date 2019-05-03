package com.fhv.weatherapp.common

object Common {

    internal const val APP_NAME = "WeatherApp"
    internal const val PERMISSION_REQUEST_ACCESS_COARSE_LOCATION = 100
    internal const val REQUEST_CHECK_SETTINGS = 0x1

    // list of all used cities
    // @JvmStatic var cityList: MutableList<City> = arrayListOf()

    // index of the city which was displayed the last
    @JvmStatic var lastCityIndex: Int = 0

}
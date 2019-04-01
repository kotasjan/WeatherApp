package com.fhv.weatherapp.model

class SmallWeather(location: String, internal var degree: String, type: String) {

    var location: String
        internal set
    var type: String
        internal set

    init {
        this.location = location
        this.type = type

    }

    fun getDegree(): String {
        return degree + "\u2103"
    }

}

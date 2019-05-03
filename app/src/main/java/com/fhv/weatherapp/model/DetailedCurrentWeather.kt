package com.fhv.weatherapp.model

import androidx.annotation.NonNull

data class DetailedCurrentWeather constructor(@NonNull var currentWeather: CurrentWeather,
                                              @NonNull var apparentTemperature: Double,
                                              @NonNull var humidity: Double,
                                              @NonNull var pressure: Double,
                                              @NonNull var cloudCover: Double,
                                              @NonNull var uvIndex: Double,
                                              @NonNull var ozone: Double)
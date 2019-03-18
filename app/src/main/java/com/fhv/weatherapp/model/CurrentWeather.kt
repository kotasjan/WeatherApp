package com.fhv.weatherapp.model

import android.support.annotation.NonNull

data class CurrentWeather constructor(@NonNull var temperature: Double,
                                      @NonNull var icon: String,
                                      @NonNull var summary: String,
                                      @NonNull var windSpeed: Double,
                                      @NonNull var precipProbability: Double)
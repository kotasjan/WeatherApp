package com.fhv.weatherapp.model

import android.support.annotation.NonNull

data class CurrentLocation constructor(@NonNull var city: String,
                                @NonNull var lat: Double,
                                @NonNull var lng: Double)
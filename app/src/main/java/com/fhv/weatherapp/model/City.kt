package com.fhv.weatherapp.model

import android.support.annotation.NonNull
import android.support.annotation.Nullable

data class City constructor(@Nullable var weather: Weather?,
                            @NonNull var location: CurrentLocation)
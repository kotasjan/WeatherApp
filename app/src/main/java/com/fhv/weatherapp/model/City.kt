package com.fhv.weatherapp.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable

data class City constructor(@Nullable var weather: Weather?,
                            @NonNull var location: CurrentLocation)
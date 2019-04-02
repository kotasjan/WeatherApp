package com.fhv.weatherapp.model

import android.location.Location
import android.support.annotation.NonNull

data class City constructor(@NonNull var Weather: Weather,
                       @NonNull var Location: Location)
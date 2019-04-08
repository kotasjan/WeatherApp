package com.fhv.weatherapp.model

import android.support.annotation.NonNull

data class City constructor(@NonNull var Weather: Weather,
                       @NonNull var Location: CurrentLocation)
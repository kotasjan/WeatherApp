package com.fhv.weatherapp.model

import androidx.annotation.NonNull

data class HourlyWeather constructor(@NonNull var precipProbabilities: List<Entry>,
                                     @NonNull var temperatures: List<Entry>) {

    data class Entry constructor(@NonNull var i: Int,
                                 @NonNull var hour: Int,
                                 @NonNull var value: Double)
}
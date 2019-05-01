package com.fhv.weatherapp.model

import androidx.annotation.NonNull

data class DailyWeather constructor(@NonNull var days: List<Entry>,
                                    @NonNull var summary: String) {

    data class Entry constructor(@NonNull var dayOfWeek: Int,
                                 @NonNull var icon: String,
                                 @NonNull var minTemperature: Double,
                                 @NonNull var maxTemperature: Double)
}
package com.fhv.weatherapp.model

import android.support.annotation.NonNull
import org.joda.time.LocalDate

data class DailyWeather constructor(@NonNull var days: List<Entry>,
                                    @NonNull var summary: String) {

    data class Entry constructor(@NonNull var date: LocalDate,
                                 @NonNull var icon: String,
                                 @NonNull var minTemperature: Double,
                                 @NonNull var maxTemperature: Double)

}
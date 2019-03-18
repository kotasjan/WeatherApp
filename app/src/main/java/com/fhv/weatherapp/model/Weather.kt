package com.fhv.weatherapp.model

import android.support.annotation.NonNull

data class Weather constructor(@NonNull var currentWeather: CurrentWeather,
                               @NonNull var detailedCurrentWeather: DetailedCurrentWeather,
                               @NonNull var hourlyWeather: HourlyWeather,
                               @NonNull var dailyWeather: DailyWeather)
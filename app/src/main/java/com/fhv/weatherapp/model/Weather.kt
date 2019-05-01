package com.fhv.weatherapp.model

import androidx.annotation.NonNull

data class Weather constructor(@NonNull var currentWeather: CurrentWeather,
                               @NonNull var detailedCurrentWeather: DetailedCurrentWeather,
                               @NonNull var hourlyWeather: HourlyWeather,
                               @NonNull var dailyWeather: DailyWeather)
package com.k.sekiro.weatherapp.domain


data class WeatherInfo(
    val  weatherDataPerDay: Map<Int, List<WeatherData>>,
    val currentWeatherData:WeatherData?,
    val tempIntervals: List<TempData?>
)

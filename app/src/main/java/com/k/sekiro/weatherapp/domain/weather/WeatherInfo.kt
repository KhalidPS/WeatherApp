package com.k.sekiro.weatherapp.domain.weather


data class WeatherInfo(
    val  weatherDataPerDay: Map<Int, List<WeatherData>>,
    val currentWeatherData:WeatherData?,
    val tempIntervals: List<TempData?>
)

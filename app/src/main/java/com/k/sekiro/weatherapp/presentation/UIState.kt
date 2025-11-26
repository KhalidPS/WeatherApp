package com.k.sekiro.weatherapp.presentation

import com.k.sekiro.weatherapp.domain.weather.WeatherInfo

data class UIState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val cityName: String = "NewYork" ,
    val countryName: String = "",
)

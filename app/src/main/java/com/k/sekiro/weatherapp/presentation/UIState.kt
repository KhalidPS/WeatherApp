package com.k.sekiro.weatherapp.presentation

import com.k.sekiro.weatherapp.domain.WeatherInfo
import com.k.sekiro.weatherapp.domain.location.Place

data class UIState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val cityName: String = "NewYork" ,
    val countryName: String = "",
    val suggestionPlaces:List<Place> = emptyList(),
)

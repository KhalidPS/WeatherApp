package com.k.sekiro.weatherapp.domain

import com.k.sekiro.weatherapp.data.remote.WeatherDto
import com.k.sekiro.weatherapp.domain.location.PlaceSuggestion
import com.k.sekiro.weatherapp.domain.util.NetworkError
import com.k.sekiro.weatherapp.domain.util.Result
import kotlinx.coroutines.Deferred

interface WeatherDataSource {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Result<WeatherInfo, NetworkError>

    fun getLocationCity(
        latitude: Double, longitude: Double, onSuccess: (city: String, country: String) -> Unit,
    )

    suspend fun getPlaceSuggestion(query: String): Result<PlaceSuggestion, NetworkError>

}
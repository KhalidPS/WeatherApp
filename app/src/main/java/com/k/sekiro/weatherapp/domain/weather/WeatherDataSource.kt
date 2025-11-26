package com.k.sekiro.weatherapp.domain.weather

import com.k.sekiro.weatherapp.domain.util.NetworkError
import com.k.sekiro.weatherapp.domain.util.Result

interface WeatherDataSource {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Result<WeatherInfo, NetworkError>

    fun getLocationCity(
        latitude: Double, longitude: Double, onSuccess: (city: String, country: String) -> Unit,
    )

}
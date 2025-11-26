package com.k.sekiro.weatherapp.domain.location

import kotlinx.serialization.Serializable

@Serializable
data class PlaceProperties(
    val country: String?,
    val city: String?,
    val state: String?,
    val lon: Double?,
    val lat: Double?
)
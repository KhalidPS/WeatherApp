package com.k.sekiro.weatherapp.domain.location

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val properties: PlaceProperties
)

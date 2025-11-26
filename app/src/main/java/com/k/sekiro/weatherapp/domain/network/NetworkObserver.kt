package com.k.sekiro.weatherapp.domain.network

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {
    val isConnected: Flow<Boolean>
}
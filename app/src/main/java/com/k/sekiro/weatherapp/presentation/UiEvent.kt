package com.k.sekiro.weatherapp.presentation

sealed interface UiEvent {
    data class ShowToast(val message: String): UiEvent
}
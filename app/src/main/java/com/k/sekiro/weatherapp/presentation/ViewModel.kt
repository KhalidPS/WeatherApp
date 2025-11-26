package com.k.sekiro.weatherapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k.sekiro.weatherapp.domain.WeatherDataSource
import com.k.sekiro.weatherapp.domain.location.LocationTracker
import com.k.sekiro.weatherapp.domain.util.onError
import com.k.sekiro.weatherapp.domain.util.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(
    private val weatherDataSource: WeatherDataSource,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    private var  job: Job? = null
    val state = _state
        .onStart {
            getWeatherInfo()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            UIState()
        )


    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    private fun getWeatherInfo() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                )
            }
            withContext(Dispatchers.IO) {
                locationTracker.getCurrentLocation()?.let {

                    weatherDataSource.getLocationCity(
                        it.latitude,
                        it.longitude,
                        onSuccess ={ city, country ->
                            _state.update {
                                it.copy(
                                    cityName = city,
                                    countryName = country
                                )
                            }
                        }
                    )

                    weatherDataSource.getWeatherData(it.latitude, it.longitude)
                        .onSuccess { data ->
                            Log.e("ks","onSuccess")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    weatherInfo = data
                                )
                            }
                        }
                        .onError { error ->
                            Log.e("ks","onError")
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    weatherInfo = null
                                )
                            }

                            _event.send(UiEvent.ShowToast(error.name))
                        }

                }
            }
        }

    }

    fun getWeatherByPlace(lat: Double,long: Double,name: String){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    cityName = name
                )
            }

            withContext(Dispatchers.IO){

                weatherDataSource.getWeatherData(lat, long)
                    .onSuccess { data ->
                        Log.e("ks","onSuccess")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                weatherInfo = data
                            )
                        }
                    }
                    .onError { error ->
                        Log.e("ks","onError")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                weatherInfo = null
                            )
                        }

                        _event.send(UiEvent.ShowToast(error.name))
                        getWeatherInfo()
                    }
            }
        }
    }

    fun getPlacesSuggestions(query: String) {
        job?.cancel()
        viewModelScope.launch {
            if (job != null) delay(500)
            job = launch(Dispatchers.IO) {
                weatherDataSource.getPlaceSuggestion(query)
                    .onSuccess { places ->
                        Log.e("ks",places.features.toString())
                        _state.update {
                            it.copy(
                                suggestionPlaces = places.features)
                        }
                    }
            }
        }

    }
}


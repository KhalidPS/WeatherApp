package com.k.sekiro.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k.sekiro.weatherapp.presentation.home_screen.HomeScreen
import com.k.sekiro.weatherapp.presentation.ObserveAsEvent
import com.k.sekiro.weatherapp.presentation.permission_screen.PermissionGate
import com.k.sekiro.weatherapp.presentation.UiEvent
import com.k.sekiro.weatherapp.presentation.ViewModel
import com.k.sekiro.weatherapp.ui.theme.WeatherAppTheme
import com.stadiamaps.autocomplete.AutoCompleteSearchBar
import com.stadiamaps.autocomplete.AutocompleteSearch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: ViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                PermissionGate {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        ObserveAsEvent(viewModel.event) { event ->
                            when(event){
                                is UiEvent.ShowToast -> {
                                    Toast.makeText(this,"${event.message} error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        val state = viewModel.state.collectAsStateWithLifecycle()
                        Log.e("ks","state: $state")
                        val weatherInfo = state.value.weatherInfo
                        val currentWeather = weatherInfo?.currentWeatherData
                        val suggestedPlaces = state.value.suggestionPlaces



                        Column(
                            modifier = Modifier.padding(innerPadding)
                        ) {

                            if (state.value.isLoading){
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    CircularProgressIndicator()
                                }
                            }else{
                                HomeScreen(
                                    weatherInfo = weatherInfo?:return@Column,
                                    currentWeather = currentWeather ?: return@Column,
                                    countryName = state.value.countryName,
                                    cityName = state.value.cityName,
                                    onPlaceClicked = { lat , long , name->
                                        viewModel.getWeatherByPlace(lat,long,name)
                                    },
                                    onQueryChange = viewModel::getPlacesSuggestions,
                                    places = suggestedPlaces
                                )
                            }

                        }
                    }
                }

            }
        }
    }
}

package com.k.sekiro.weatherapp

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.k.sekiro.weatherapp.presentation.home_screen.HomeScreen
import com.k.sekiro.weatherapp.presentation.ObserveAsEvent
import com.k.sekiro.weatherapp.presentation.permission_screen.PermissionGate
import com.k.sekiro.weatherapp.presentation.UiEvent
import com.k.sekiro.weatherapp.presentation.ViewModel
import com.k.sekiro.weatherapp.ui.theme.WeatherAppTheme
import io.ktor.util.rootCause
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: ViewModel by viewModel()

    private  var resolutionLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        resolutionLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ){ result ->
            when(result.resultCode){
                RESULT_OK -> {

                }

                RESULT_CANCELED -> {

                }


            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                PermissionGate {

                    if (!gpsProviderEnabled()){
                        promptUserToEnableGps()
                    }

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        ObserveAsEvent(viewModel.event) { event ->
                            when(event){
                                is UiEvent.ShowToast -> {
                                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        val state = viewModel.state.collectAsStateWithLifecycle()
                        Log.e("ks","state: $state")
                        val weatherInfo = state.value.weatherInfo
                        val currentWeather = weatherInfo?.currentWeatherData


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

                                )
                            }

                        }
                    }
                }

            }
        }
    }



    private fun gpsProviderEnabled(): Boolean{
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun promptUserToEnableGps(){
        val locationRequest = LocationRequest.Builder(10000L).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionLauncher?.launch(intentSenderRequest)
                }catch (ex : IntentSender.SendIntentException){

                }
            }
        }
    }

}



package com.k.sekiro.weatherapp.data.remote

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.location.Address
import androidx.compose.ui.geometry.Rect
import com.k.sekiro.weatherapp.BuildConfig
import com.k.sekiro.weatherapp.data.mapper.toWeatherInfo
import com.k.sekiro.weatherapp.data.util.safeCall
import com.k.sekiro.weatherapp.domain.WeatherDataSource
import com.k.sekiro.weatherapp.domain.WeatherInfo
import com.k.sekiro.weatherapp.domain.location.PlaceSuggestion
import com.k.sekiro.weatherapp.domain.util.EmptyResult
import com.k.sekiro.weatherapp.domain.util.NetworkError
import com.k.sekiro.weatherapp.domain.util.Result
import com.k.sekiro.weatherapp.domain.util.map
import com.k.sekiro.weatherapp.domain.util.onError
import com.k.sekiro.weatherapp.domain.util.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parametersOf
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.koin.core.parameter.parametersOf
import java.util.Locale
import kotlin.coroutines.coroutineContext
import kotlin.math.log


class WeatherDataSourceImpl(
    private val httpClient: HttpClient,
    private val context: Context,
) : WeatherDataSource {

    private val url =
        "https://api.open-meteo.com/v1/forecast?hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,pressure_msl"

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Result<WeatherInfo, NetworkError> {
        return safeCall<WeatherDto> {
            httpClient.get(url) {
                //  parametersOf("latitude" to latitude, "longitude" to longitude)
                parameter("latitude", latitude)
                parameter("longitude", longitude)
            }
        }.map{
            it.toWeatherInfo()
        }
    }

    override fun getLocationCity(
        latitude: Double,
        longitude: Double,
        onSuccess: (city: String,country: String) -> Unit,
        ) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latitude, longitude, 1, { addresses ->
                        if (!addresses.isNullOrEmpty()) {
                            val address: Address = addresses[0]
                            val locationName: String? =
                                address.getAddressLine(0) // Get the full address line
                            // You can also get other details like city, country, etc.
                            val city: String? = address.locality
                            val country: String? = address.countryName

                            onSuccess(city ?: "",country ?: "")

                        }
                    })
                } else {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1) // Request 1 result
                    if (!addresses.isNullOrEmpty()) {
                        val address: Address = addresses[0]
                        val locationName: String? = address.getAddressLine(0) // Get the full address line
                        // You can also get other details like city, country, etc.
                        val city: String? = address.locality
                        val country: String? = address.countryName

                        onSuccess(city ?: "",country ?: "")

                        // Use the locationName, city, or country as needed
                    } else {
                        // Handle cases where no address is found for the given coordinates
                    }
                }


            } catch (e: IOException) {
                // Handle network or I/O errors
            }

    }

    override suspend fun getPlaceSuggestion(query: String): Result<PlaceSuggestion, NetworkError> {
        val url = "https://api.geoapify.com/v1/geocode/autocomplete?text=$query&apiKey=${BuildConfig.GEOAPIFY_API_KEY}"
        coroutineContext.ensureActive()
        return safeCall {
            httpClient.get(url)
        }
    }


}
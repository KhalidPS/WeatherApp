package com.k.sekiro.weatherapp.presentation.home_screen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.k.sekiro.weatherapp.BuildConfig
import com.k.sekiro.weatherapp.R
import com.k.sekiro.weatherapp.domain.WeatherData
import com.k.sekiro.weatherapp.domain.WeatherInfo
import com.k.sekiro.weatherapp.presentation.home_screen.compnent.HomeSearchBar
import com.k.sekiro.weatherapp.presentation.home_screen.compnent.TemperatureCard
import com.k.sekiro.weatherapp.presentation.home_screen.compnent.TodayForecast
import com.k.sekiro.weatherapp.presentation.home_screen.compnent.WeatherItem
import com.stadiamaps.autocomplete.AutocompleteSearch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherInfo: WeatherInfo,
    currentWeather: WeatherData,
    cityName: String,
    countryName: String,
    onPlaceClicked:(Double, Double, String) -> Unit = { _, _,_ ->},
) {

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
        ) {

            Box{

                Crossfade(currentWeather.weatherType) { type ->

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(type.gifBackground)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .graphicsLayer{
                                alpha = 0.5f
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 8.dp, end =8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                   /* HomeSearchBar(
                        places = listOf("sunny","snowy","rainy","thunder"),
                        onPlaceClicked = {
                            onPlaceClicked(it)
                        }
                    )*/
                    AutocompleteSearch(
                        apiKey = BuildConfig.STADIA_API_KEY,
                        onFeatureClicked = { featurePropertiesV2 ->
                             val coordinates = featurePropertiesV2.geometry?.coordinates
                                coordinates?.let{
                                    Log.e("ks","name : ${featurePropertiesV2.properties.name}")

                                    Log.e("ks","coordinates : $it")
                                    val lat = it[0]
                                    val long = it[1]
                                    val name = featurePropertiesV2.properties.name
                                    onPlaceClicked(lat,long,name)
                                }
                        }
                    )

                    Spacer(Modifier.height(12.dp))


                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "location",
                                tint = Color.Red
                            )

                            Text(
                                text = cityName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        Text("Today ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}")
                    }


/////////////////////////////////////////////////////


                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "${currentWeather.temperatureCelsius}°C",
                            fontSize = 60.sp
                        )

                        Text(currentWeather.weatherType.weatherDesc)
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        WeatherItem(
                            icon = R.drawable.ic_pressure,
                            text = "${currentWeather.pressure.roundToInt()}hpa",

                            )
                        WeatherItem(
                            icon = R.drawable.ic_drop,
                            text = "${currentWeather.humidity.roundToInt()}%",
                            iconSize = 20.dp
                        )
                        WeatherItem(
                            icon = R.drawable.ic_wind,
                            text = "${currentWeather.windSpeed.roundToInt()}km/h"
                        )


                    }

                    Spacer(Modifier.height(12.dp))


                    TemperatureCard(
                        data = weatherInfo.tempIntervals
                    )

                    Spacer(Modifier.height(16.dp))

                    TodayForecast(
                        weatherInfo = weatherInfo
                    )

                }

            }


        }
    }
}


/*@Preview
@Composable
private fun HomeScreenPre() {
    HomeScreen(

    )
}*/
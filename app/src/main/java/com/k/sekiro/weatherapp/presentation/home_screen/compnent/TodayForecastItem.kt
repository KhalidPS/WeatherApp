package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.k.sekiro.weatherapp.R
import com.k.sekiro.weatherapp.domain.weather.WeatherData
import java.time.format.DateTimeFormatter

@SuppressLint("ResourceType")
@Composable
fun TodayForecastItem(
    data: WeatherData,
    modifier: Modifier = Modifier
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp),
    ){


        val time = data.time.format(DateTimeFormatter.ofPattern("HH:mm"))

        Text(time)

        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_sunny),
            modifier = Modifier.size(24.dp),
            contentDescription = null,
        )

        Spacer(Modifier.height(8.dp))

        Text("${data.temperatureCelsius}°C")
    }
}
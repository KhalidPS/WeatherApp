package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k.sekiro.weatherapp.domain.WeatherInfo

@Composable
fun TodayForecast(
    modifier: Modifier = Modifier,
    weatherInfo: WeatherInfo
) {

    val data = remember(weatherInfo){ weatherInfo.weatherDataPerDay[0]?: return@remember emptyList()  }

    Column {
        Row(modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Today",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            Modifier.fillMaxWidth(),
        ) {
            items(data){ item ->

                TodayForecastItem(
                    data = item
                )
            }
        }
    }


}
package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k.sekiro.weatherapp.domain.TempData
import com.k.sekiro.weatherapp.ui.theme.Purple80


@Composable
fun TemperatureCard(
    data: List<TempData?> = temperatureData
) {
    Box(
        Modifier.clip(RoundedCornerShape(12.dp))
            .background(Purple80.copy(0.3f))
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {

            Text(
                text = "Temperature",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            SmoothPathChart(
                color = Purple80,
                data = data
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                data.forEach { temp ->
                    IntervalItem(
                        temp?.interval ?: return@Row,
                        temp.temp
                    )
                }
            }



        }
    }

}


@Preview
@Composable
private fun TemperatureCardPrev() {
    TemperatureCard()
}


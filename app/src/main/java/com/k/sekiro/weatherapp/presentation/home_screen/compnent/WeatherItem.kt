package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun WeatherItem(
    @DrawableRes icon: Int,
    text: String,
    iconSize: Dp = 24.dp,
    iconTint: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
        )
    }
}
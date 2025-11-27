package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.k.sekiro.weatherapp.domain.weather.TempData
import com.k.sekiro.weatherapp.ui.theme.Purple80
import kotlin.contracts.ExperimentalContracts

// 1. Define the Data

val temperatureData = listOf(
    TempData("Morning", 20),
    TempData("Afternoon", 18),
    TempData("Evening", 23),
    TempData("Night", 15)
)

@OptIn(ExperimentalContracts::class)
@Composable
fun TemperaturePathChart(
    color: Color = Purple80,
    data: List<TempData?> = temperatureData) {

    if (data.isEmpty() || data.first() == null) return

    // Determine the min/max temps for scaling
    val minTemp = data.minOf { it!!.temp } - 2
    val maxTemp = data.maxOf { it!!.temp } + 2

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)) {
        val width = size.width
        val height = size.height

        val yCoords = data.map { item ->
            val normalizedY = (item!!.temp - minTemp) / (maxTemp - minTemp).toFloat()

            height * (1f - normalizedY)
        }

        val stepX = width / (data.size - 1).toFloat()
        val xCoords = data.indices.map { it * stepX }

        val points = xCoords.zip(yCoords).map { (x, y) -> Offset(x, y) }

        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                val p1 = points[i - 1]
                val p2 = points[i]

                val controlX1 = p1.x + (p2.x - p1.x) * 0.5f
                val controlY1 = p1.y
                val controlX2 = p2.x - (p2.x - p1.x) * 0.5f
                val controlY2 = p2.y

                path.cubicTo(
                    x1 = controlX1, y1 = controlY1,
                    x2 = controlX2, y2 = controlY2,
                    x3 = p2.x, y3 = p2.y
                )
            }
        }

        drawPath(
            path = path,
            color = color, // Light Blue color
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        points.forEachIndexed { index,offset ->
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = if (index == 0){
                    offset.plus(Offset(10.dp.toPx(),0f))
                }else if (index == points.size - 1){
                    offset.minus(Offset(10.dp.toPx(),0f))
                }else offset,

                style = Stroke(width = 2.dp.toPx())
            )

        }

    }
}

@Preview
@Composable
private fun SmoothPathChartPrev() {
    TemperaturePathChart()
}

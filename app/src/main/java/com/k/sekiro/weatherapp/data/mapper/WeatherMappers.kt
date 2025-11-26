package com.k.sekiro.weatherapp.data.mapper

import com.k.sekiro.weatherapp.data.remote.WeatherDataDto
import com.k.sekiro.weatherapp.data.remote.WeatherDto
import com.k.sekiro.weatherapp.domain.weather.WeatherData
import com.k.sekiro.weatherapp.domain.weather.WeatherInfo
import com.k.sekiro.weatherapp.domain.weather.WeatherType
import com.k.sekiro.weatherapp.domain.weather.TempData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>>{
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                temperatureCelsius = temperature,
                weatherType = WeatherType.fromWMO(weatherCode)
            )
        )
    }.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo{
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if (now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }

    val morning = weatherDataMap[0]?.first {
        it.time.hour < 12
    }?.let {
        TempData("Morning",it.temperatureCelsius.roundToInt())
    }
    val afternoon = weatherDataMap[0]?.first {
        it.time.hour in 12..18
    }?.let {
        TempData("Afternoon",it.temperatureCelsius.roundToInt())
    }

    val evening = weatherDataMap[0]?.first {
        it.time.hour > 18
    }?.let {
        TempData("Evening",it.temperatureCelsius.roundToInt())
    }

    val night = weatherDataMap[0]?.first {
        it.time.hour in 21..23 || it.time.hour in 0..6
    }?.let {
        TempData("Night",it.temperatureCelsius.roundToInt())
    }


    val tempIntervals = listOf(morning, afternoon, evening, night)

    return WeatherInfo(
        currentWeatherData = currentWeatherData,
        weatherDataPerDay = weatherDataMap,
        tempIntervals = tempIntervals
    )
}
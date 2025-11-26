package com.k.sekiro.weatherapp.di

import com.google.android.gms.location.LocationServices
import com.k.sekiro.weatherapp.data.location.DefaultLocationTracker
import com.k.sekiro.weatherapp.data.remote.WeatherDataSourceImpl
import com.k.sekiro.weatherapp.data.util.HttpClientFactory
import com.k.sekiro.weatherapp.domain.WeatherDataSource
import com.k.sekiro.weatherapp.domain.location.LocationTracker
import com.k.sekiro.weatherapp.presentation.ViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.math.sin


val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }

    singleOf(::WeatherDataSourceImpl) bind WeatherDataSource::class

    viewModelOf(::ViewModel)

    single {
        LocationServices.getFusedLocationProviderClient(androidContext())
    }

    singleOf(::DefaultLocationTracker) bind LocationTracker::class
}
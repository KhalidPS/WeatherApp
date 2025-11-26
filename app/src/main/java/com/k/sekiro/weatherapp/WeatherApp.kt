package com.k.sekiro.weatherapp

import android.app.Application
import com.k.sekiro.weatherapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
            androidContext(this@WeatherApp)
        }
    }
}
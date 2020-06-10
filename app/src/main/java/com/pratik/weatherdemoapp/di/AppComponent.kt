package com.pratik.weatherdemoapp.di

import com.pratik.weatherdemoapp.WeatherHomeActivity
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import com.pratik.weatherdemoapp.retrofit.RetrofitModule
import com.pratik.weatherdemoapp.viewmodel.WeatherReportModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RetrofitModule::class,
        WeatherReportModule::class
    ]
)
interface AppComponent {

    fun inject(activity: WeatherHomeActivity)
}
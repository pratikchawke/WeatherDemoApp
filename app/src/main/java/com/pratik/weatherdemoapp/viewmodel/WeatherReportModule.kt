package com.pratik.weatherdemoapp.viewmodel

import com.pratik.weatherdemoapp.retrofit.ApiRequest
import dagger.Module
import dagger.Provides

@Module
class WeatherReportModule {
    @Provides
    fun providesMainViewModelFactory(apiRequest: ApiRequest): WeatherReportModelFactory {
        return WeatherReportModelFactory(apiRequest)
    }
}
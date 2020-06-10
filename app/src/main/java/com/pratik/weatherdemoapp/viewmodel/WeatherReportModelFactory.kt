package com.pratik.weatherdemoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import javax.inject.Inject

class WeatherReportModelFactory(private val apiRequest: ApiRequest) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherReportModel::class.java)) {
            return WeatherReportModel(apiRequest) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.pratik.weatherdemoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.data
import com.pratik.weatherdemoapp.model.WeatherReport

class WeatherReportModel : ViewModel() {

    var weatherReportdata : LiveData<WeatherReport> = data!!

}
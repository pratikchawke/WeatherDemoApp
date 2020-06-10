package com.pratik.weatherdemoapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pratik.weatherdemoapp.AppConstants
import com.pratik.weatherdemoapp.WeatherHomeActivity
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.loader
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.longitude
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherReportModel(private val apiRequest: ApiRequest) : ViewModel() {
    private val TAG = WeatherReportModel::class.java.simpleName

    val weatherReportData: LiveData<WeatherReport>
        get() = mutableWeatherReportData
    var mutableWeatherReportData: MutableLiveData<WeatherReport> = MutableLiveData()


    fun getWeatherReport() {
        loader.showLoading()
        apiRequest.getWeatherReport(
            AppConstants.API_KEY,
            WeatherHomeActivity.latitude, longitude, AppConstants.UNITS
        )
            ?.enqueue(object : Callback<WeatherReport?> {
                override fun onResponse(
                    call: Call<WeatherReport?>,
                    response: Response<WeatherReport?>
                ) {
                    Log.d(TAG, "Response : " + response.body())
                    if (response.body() != null) {
                        loader.dismissLoading()
                        mutableWeatherReportData!!.value = response.body()
                    }
                }

                override fun onFailure(call: Call<WeatherReport?>, t: Throwable) {
                    Log.d(TAG, "Error Msg : " + t)
                    Log.d(TAG, t.cause?.message)
                    loader.dismissLoading()
                }
            })
    }
}
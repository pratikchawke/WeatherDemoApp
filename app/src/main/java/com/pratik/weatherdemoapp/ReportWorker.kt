package com.pratik.weatherdemoapp

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.data
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.loader
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.longitude
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import com.pratik.weatherdemoapp.retrofit.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private var apiRequest: ApiRequest =
        RetrofitBuilder.retrofitInstance!!.create(ApiRequest::class.java)

    override fun doWork(): Result {
        Log.d("ReportWorker","doWork in background !!!")
        if (CacheManager.getInstance(context).getString(AppConstants.REPORT) == null)
            getWeatherReport()
        else if (Utils.isMetered(context))
            getWeatherReport()
        return Result.success()
    }

    private fun getWeatherReport() {
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
                    Log.d("Pratik", "Response : " + response.body())
                    if (response.body() != null) {
                        loader.dismissLoading()
                        data!!.value = response.body()
                        val gson = Gson()
                        val jsonObject = gson.toJson(response.body())
                        Log.d("Pratik", "jsonObject : " + jsonObject)
                        CacheManager.getInstance(context)
                            .putString(AppConstants.REPORT, "" + jsonObject)
                    }
                }

                override fun onFailure(call: Call<WeatherReport?>, t: Throwable) {
                    Log.d("Pratik1", "Error Msg : " + t)
                    Log.d("Pratik1", t.cause?.message)
                    loader.dismissLoading()
                }
            })
    }
}
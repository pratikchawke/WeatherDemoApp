package com.pratik.weatherdemoapp

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pratik.weatherdemoapp.WeatherHomeActivity.Companion.longitude
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.retrofit.ApiRequest
import com.pratik.weatherdemoapp.retrofit.RetrofitModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val TAG = ReportWorker::class.java.simpleName
    private val module = RetrofitModule()
    private var apiRequest: ApiRequest =
        module.createApiRequest(module.getRetrofit())

    override fun doWork(): Result {
        Log.d(TAG, "doWork in background !!!")
        if (CacheManager.getInstance(context).getString(AppConstants.REPORT) == null)
            getWeatherReport()
        else if (Utils.isWifiConnected(context))
            getWeatherReport()
        return Result.success()
    }

    private fun getWeatherReport() {
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
                        Utils.addToDB(context, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<WeatherReport?>, t: Throwable) {
                    Log.d(TAG, "Error Msg : " + t)
                    Log.d(TAG, t.cause?.message)
                }
            })
    }
}
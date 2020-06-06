package com.pratik.weatherdemoapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.pratik.erostestapp.listener.LoadingListener
import com.pratik.weatherdemoapp.databinding.ActivityWeatherHomeBinding
import com.pratik.weatherdemoapp.model.Weather
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.viewmodel.WeatherReportModel
import java.util.concurrent.TimeUnit


class WeatherHomeActivity : AppCompatActivity(), LoadingListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        loader = this

        val binding: ActivityWeatherHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_weather_home)
//        setContentView(R.layout.activity_weather_home)
        if (Utils.isInternetConnected(this)) {
            processFurther()
        } else {
            Utils.showNoNetworkDialog(this)
        }

        WeatherReportModel().weatherReportdata.observe(this,
            Observer { data ->
                if (data != null)
                    binding.weatherReport = data
            })
    }

    fun sendRequest(view: View) {

        processFurther()
    }

    private fun processFurther() {
        //if data persist in local storage
        //if time spent is more than 2 hours
        //1. else get data from web services
        // 2. show local stored data


        if (CacheManager.getInstance(this).getString(AppConstants.REPORT) == null) {
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(ReportWorker::class.java, 2, TimeUnit.HOURS).build()
            val singleWorkRequest = OneTimeWorkRequest.Builder(ReportWorker::class.java).build()
            WorkManager.getInstance().enqueue(singleWorkRequest)
        }


    }

    companion object {
        lateinit var loader: LoadingListener
        var data: MutableLiveData<WeatherReport> = MutableLiveData()
    }

    override fun showLoading() {
        runOnUiThread { Utils.showLoader(this) }
    }

    override fun dismissLoading() {
        runOnUiThread { Utils.hideLoader() }
    }
}



package com.pratik.weatherdemoapp


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.pratik.erostestapp.listener.LoadingListener
import com.pratik.weatherdemoapp.databinding.ActivityWeatherHomeBinding
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.viewmodel.WeatherReportModel
import java.util.concurrent.TimeUnit


class WeatherHomeActivity : AppCompatActivity(), LoadingListener {

    private val TAG = WeatherHomeActivity::class.java.simpleName
    private val FINE_LOCATION_CODE = 1234
    private val ENABLE_LOCATION_CODE = 1235
    private var googleApiClient: GoogleApiClient? = null
    private lateinit var location: Location

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

    private fun processFurther() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Utils.isLocationPermissionGranted(this)) {
                requestLocationPermission()
            } else {
                if (!Utils.isLocationEnabled(this)) {
                    enableLocation()
                } else {
                    getData()
                }
            }
        } else {
            if (!Utils.isLocationEnabled(this)) {
                enableLocation()
            } else {
                getData()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            FINE_LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "requestCode : $requestCode")
        when (requestCode) {
            FINE_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Utils.isLocationEnabled(this)) {
                        getData()
                    } else {
                        enableLocation()
                    }
                } else {
                    //todo show custom message to user and ask for permission if it is mandatory
                }
            }
            ENABLE_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Utils.isLocationPermissionGranted(this)) {
                        getData()
                    } else {
                        requestLocationPermission()
                    }
                } else {
//                    Toast.makeText(this, "Please enable location !!!", Toast.LENGTH_LONG).show()
                    getData()
                }
            }
        }
    }

    private fun enableLocation() {

        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //                    .addConnectionCallbacks(this)
                .addOnConnectionFailedListener { connectionResult ->
                    Log.d("UserLocation error ", "" + connectionResult.errorCode)
                }.build()
            googleApiClient!!.connect()

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = (30 * 1000).toLong()
            locationRequest.fastestInterval = (5 * 1000).toLong()
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            builder.setAlwaysShow(true)

            val result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                Log.d(TAG, " status.statusCode : " + status.statusCode)
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        startIntentSenderForResult(
                            status.getResolution().intentSender,
                            ENABLE_LOCATION_CODE,
                            null,
                            0,
                            0,
                            0,
                            null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.d(TAG, "IntentSenderException : " + e.toString())
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult requestCode : $requestCode")
        when (requestCode) {
            ENABLE_LOCATION_CODE -> {
                Log.d(TAG, "onActivityResult resultCode : $resultCode : $RESULT_OK")
                if (resultCode == RESULT_OK) {
                    if (Utils.isLocationPermissionGranted(this)) {
                        getData()
                    } else {
                        requestLocationPermission()
                    }
                } else {
                    getData()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getData() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        latitude = location.latitude
        longitude = location.longitude
        if (CacheManager.getInstance(this).getString(AppConstants.REPORT) == null) {
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(ReportWorker::class.java, 2, TimeUnit.HOURS).build()
            val singleWorkRequest = OneTimeWorkRequest.Builder(ReportWorker::class.java).build()
            WorkManager.getInstance().enqueue(singleWorkRequest)
        }
    }

    companion object {
        lateinit var loader: LoadingListener
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        var data: MutableLiveData<WeatherReport> = MutableLiveData()

    }

    override fun showLoading() {
        runOnUiThread { Utils.showLoader(this) }
    }

    override fun dismissLoading() {
        runOnUiThread { Utils.hideLoader() }
    }
}



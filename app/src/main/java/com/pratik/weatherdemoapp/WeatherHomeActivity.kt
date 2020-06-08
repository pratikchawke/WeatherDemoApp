package com.pratik.weatherdemoapp


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.gson.Gson
import com.pratik.weatherdemoapp.databinding.ActivityWeatherHomeBinding
import com.pratik.weatherdemoapp.listener.LoadingListener
import com.pratik.weatherdemoapp.model.WeatherReport
import com.pratik.weatherdemoapp.viewmodel.WeatherReportModel
import java.util.concurrent.TimeUnit


class WeatherHomeActivity : AppCompatActivity(), LoadingListener {

    private val TAG = WeatherHomeActivity::class.java.simpleName
    private val FINE_LOCATION_CODE = 1234
    private val ENABLE_LOCATION_CODE = 1235
    private var googleApiClient: GoogleApiClient? = null
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loader = this

        /*
        Used data binding to bind views and display layout
         */
        val binding: ActivityWeatherHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_weather_home)

        /**
         * Check for internet connection availability
         */
        if (Utils.isInternetConnected(this)) {
            processFurther()
        } else {
            Utils.showNoNetworkDialog(this)
        }

        /*
        WeatherReportModel is a ViewModel class which holds LiveData. Whenever data is updated, it will always bind on views.
         */
        WeatherReportModel().weatherReportdata.observe(this,
            Observer { data ->
                if (data != null)
                    binding.weatherReport = data
            })
    }

    private fun processFurther() {
        /**
         * Here we check for location runtime permission and GPS is enabled
         */
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
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
                    AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage(getString(R.string.permission_required_message))
                        .setPositiveButton("Settings") { dialog, which ->
                            dialog.dismiss()
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, ENABLE_LOCATION_CODE)
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        .show()
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
        /**
         * Show user dialog box to enable gps from setting to get current location of user
         */
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
        showLoading()
        /**
         * Get last known location of user and request for updated location. Location listener gets the updated location whenever it changed
         */
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val bestProvider = locationManager!!.getBestProvider(criteria, false)
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, listener!!)
        location = locationManager.getLastKnownLocation(bestProvider)


        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }

        /**
         * Apply a periodic request with the help of WorkManger and get data from webservices. If data is present in cache then it will not create one more request
         * Data will always updated in background with 2 hours of interval.
         */
        if (CacheManager.getInstance(this).getString(AppConstants.REPORT) == null) {
            Handler().postDelayed(Runnable {

                val constraint = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .setRequiredNetworkType(NetworkType.METERED) //This constraint is not calling doWork() function of worker. Handled metered check in worker class
                    .build()

                val periodicWorkRequest =
                    PeriodicWorkRequest.Builder(ReportWorker::class.java, 2, TimeUnit.HOURS)
                        .setConstraints(constraint)
                        .build()
                WorkManager.getInstance().enqueue(periodicWorkRequest)

//                val singleWorkRequest = OneTimeWorkRequest.Builder(ReportWorker::class.java).build()
//                WorkManager.getInstance().enqueue(singleWorkRequest)

                dismissLoading()
            }, 3000)
        } else {
            val gson = Gson()
            val weatherReport: WeatherReport = gson.fromJson(
                CacheManager.getInstance(this).getString(AppConstants.REPORT),
                WeatherReport::class.java
            )
            data.value = weatherReport
            dismissLoading()
        }
    }

    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    companion object {
        lateinit var loader: LoadingListener
        var latitude: Double = AppConstants.LAT
        var longitude: Double = AppConstants.LONG
        var data: MutableLiveData<WeatherReport> = MutableLiveData()

    }

    override fun showLoading() {
        runOnUiThread { Utils.showLoader(this) }
    }

    override fun dismissLoading() {
        runOnUiThread { Utils.hideLoader() }
    }
}



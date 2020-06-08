package com.pratik.weatherdemoapp


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    private val TAG = Utils.javaClass.simpleName
    private var dialog: Dialog? = null

    fun isInternetConnected(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    fun isMetered(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isMetered = cm.isActiveNetworkMetered
        return isMetered
    }

    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false
    }

    fun showNoNetworkDialog(context: Activity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage(R.string.no_internet_connected)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Close") { dialogInterface, which ->
            context.finish()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun hideLoader() {
        Log.d(TAG, "Hide Loading !!!!")
        try {
            if (dialog!! != null) {
                if (dialog!!.isShowing) dialog!!.dismiss()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception $e")
        }
    }

    fun showLoader(context: Context) {
        Log.d(TAG, "Show Loading !!!!")
        try {
            dialog = getProgressDialog(context)
            dialog!!.show()
        } catch (e: Exception) {
            Log.d(TAG, "Exception $e")
        }
    }

    fun getProgressDialog(context: Context): Dialog {
        if (dialog == null) {
            dialog = Dialog(context)
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        val factory = LayoutInflater.from(context)
        val customPopupView: View = factory.inflate(R.layout.loading_dialog, null)
        dialog!!.setContentView(customPopupView)
        return dialog!!
    }

    fun getFormattedTime(time: Long, context: Context): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone//get your local time zone.
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        sdf.timeZone = tz//set time zone.
        val localTime = sdf.format(time)
        var date = Date()
        try {
            date = sdf.parse(localTime)//get local date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date.toString()
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun isLocationEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
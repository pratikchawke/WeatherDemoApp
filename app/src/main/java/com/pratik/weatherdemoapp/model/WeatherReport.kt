package com.pratik.weatherdemoapp.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.pratik.weatherdemoapp.AppConstants
import com.pratik.weatherdemoapp.Utils
import com.pratik.weatherdemoapp.model.*
import javax.inject.Inject

data class WeatherReport (
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
) {
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, image_path: String?) {
            Glide.with(view.context)
                .load(AppConstants.BASE_IMAGE_URL + image_path + ".png")
                .into(view)
        }

        @JvmStatic
        @BindingAdapter("description")
        fun setDescription(view: TextView, description: String?) {
            view.text = description
        }

        @JvmStatic
        @BindingAdapter("current_temperature")
        fun setCurrentTemperature(view: TextView, current_temperature: Double?) {
            view.text = current_temperature.toString() + "°C"
        }

        @JvmStatic
        @BindingAdapter("min_temperature")
        fun setMinTemperature(view: TextView, min_temperature: Double?) {
            view.text = "Minimum : " + min_temperature.toString() + "°C"
        }

        @JvmStatic
        @BindingAdapter("max_temperature")
        fun setMaxTemperature(view: TextView, max_temperature: Double?) {
            view.text = "Maximum : " + max_temperature.toString() + "°C"
        }

        @JvmStatic
        @BindingAdapter("city")
        fun setCity(view: TextView, city: String?) {
            view.text = city
        }

        @JvmStatic
        @BindingAdapter("pressure")
        fun setPressure(view: TextView, pressure: Int?) {
            view.text = "Pressure \n" + pressure
        }

        @JvmStatic
        @BindingAdapter("clouds")
        fun setClouds(view: TextView, clouds: Int?) {
            view.text = "Cloud \n" + clouds
        }

        @JvmStatic
        @BindingAdapter("humidity")
        fun setHumidity(view: TextView, humidity: Int?) {
            view.text = "Humidity \n" + humidity
        }

        @JvmStatic
        @BindingAdapter("wind")
        fun setWind(view: TextView, wind: Double?) {
            view.text = "Wind Speed \n" + wind
        }

        @JvmStatic
        @BindingAdapter("sunset")
        fun setSunset(view: TextView, sunset: Int?) {
            view.text = "Sunset \n" + Utils.getFormattedTime(sunset!!.toLong() * 1000,view.context)
        }

        @JvmStatic
        @BindingAdapter("sunrise")
        fun setSunrise(view: TextView, sunrise: Int?) {
            view.text = "Sunrise \n" +  Utils.getFormattedTime(sunrise!!.toLong()* 1000,view.context)
        }
    }

    override fun toString(): String {
        return "WeatherReport(base='$base', clouds=$clouds, cod=$cod, coord=$coord, dt=$dt, id=$id, main=$main, name='$name', sys=$sys, timezone=$timezone, visibility=$visibility, weather=$weather, wind=$wind)"
    }

}


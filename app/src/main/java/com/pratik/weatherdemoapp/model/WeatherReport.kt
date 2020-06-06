package com.pratik.weatherdemoapp.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.pratik.weatherdemoapp.AppConstants
import com.pratik.weatherdemoapp.model.*

data class WeatherReport(
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
){
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, image_path: String?) {
            Glide.with(view.context)
                .load(AppConstants.BASE_IMAGE_URL + image_path)
                .into(view)
        }
        @JvmStatic
        @BindingAdapter("description")
        fun showMovieDescription(view: TextView, description: String?) {
            view.text = description
        }

        @JvmStatic
        @BindingAdapter("current_temperature")
        fun setCurrentTemperature(view: TextView, current_temperature: Double?) {
            view.text = current_temperature.toString()
        }
    }
}


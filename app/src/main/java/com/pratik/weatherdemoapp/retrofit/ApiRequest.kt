package com.pratik.erostestapp.retrofit
import com.pratik.weatherdemoapp.model.WeatherReport
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface  ApiRequest {
    @GET("/data/2.5/weather?")
    fun getWeatherReport(
        @Query("appid") apiKey: String?,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?
    ): Call<WeatherReport>
}
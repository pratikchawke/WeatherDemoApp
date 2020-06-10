package com.pratik.weatherdemoapp

class AppConstants {

    companion object {
        const val TABLE_NAME = "weather"
        val REPORT = "report"
        val LAT = 19.206859
        val LONG = 73.095866
        val UNITS = "metric"
        val API_KEY: String = "8118ed6ee68db2debfaaa5a44c832918"
        val BASE_URL = "http://api.openweathermap.org"
        val BASE_IMAGE_URL: String = "http://openweathermap.org/img/w/"

        var requestUrl: String =
            "https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LONG&units=metric&appid=$API_KEY"
//            "https://api.openweathermap.org/data/2.5/weather?lat=19.206859&lon=73.095866&units=metric&appid=8118ed6ee68db2debfaaa5a44c832918"
    }
}
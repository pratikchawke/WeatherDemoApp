package com.pratik.weatherdemoapp.application

import android.app.Application
import androidx.room.Room
import com.pratik.weatherdemoapp.AppConstants
import com.pratik.weatherdemoapp.db.AppDatabase
import com.pratik.weatherdemoapp.di.AppComponent
import com.pratik.weatherdemoapp.di.DaggerAppComponent

class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent
            .builder()
            .build()

        db = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, AppConstants.TABLE_NAME).allowMainThreadQueries()
            .build()
    }
}

lateinit var component: AppComponent
lateinit var db: AppDatabase
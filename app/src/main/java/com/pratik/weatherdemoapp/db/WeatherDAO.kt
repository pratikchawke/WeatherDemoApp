package com.pratik.weatherdemoapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pratik.weatherdemoapp.AppConstants


@Dao
interface WeatherDAO {
    @get:Query("SELECT * FROM WeatherEntity")
    val weatherEntityList: List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg weatherEntity: WeatherEntity)
}
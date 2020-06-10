package com.pratik.weatherdemoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity(
        @field:PrimaryKey
        val id: Int,
        val report: String
)
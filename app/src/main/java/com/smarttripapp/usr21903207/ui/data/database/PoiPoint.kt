package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_points")
data class PoiPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

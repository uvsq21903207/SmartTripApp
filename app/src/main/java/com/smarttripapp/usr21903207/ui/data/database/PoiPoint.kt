package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poi_points")
data class PoiPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long, // Pour lier le POI à un voyage spécifique
    val name: String,
    val description: String,
    val type: String, // Catégorie: Restaurant, Musée, Monument, etc.
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val photoPath: String? = null // Chemin vers la photo
)
package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


 // Représente un point de localisation enregistré dans la base de données.
 // Chaque instance de cette classe correspond à une ligne dans la table "location_points".
@Entity(tableName = "location_points") // Nom de la table dans la base de données
data class LocationPoint(
    @PrimaryKey(autoGenerate = true) // Clé primaire auto-incrémentée
    val id: Long = 0,

    val latitude: Double,       // Coordonnée de latitude
    val longitude: Double,      // Coordonnée de longitude
    val altitude: Double,       // Altitude (si disponible)
    val accuracy: Float,        // Précision de la localisation en mètres
    val speed: Float,           // Vitesse en mètres par seconde (si disponible)
    val timestamp: Long         // Moment où la localisation a été enregistrée (en millisecondes depuis l'époque Unix)

)

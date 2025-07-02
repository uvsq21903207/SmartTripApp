package com.smarttripapp.usr21903207.ui.data.repository

import android.location.Location
import com.smarttripapp.usr21903207.ui.data.database.LocationDao
import com.smarttripapp.usr21903207.ui.data.database.LocationPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext


 //Repository pour gérer les données de localisation.
class LocationRepository(private val locationDAO: LocationDao) {

     //Récupère tous les points de localisation sous forme de Flow.
    val allLocations: Flow<List<LocationPoint>> = locationDAO.getAllLocations()


     // Récupère le dernier point de localisation sous forme de Flow.

    val lastLocation: Flow<LocationPoint?> = locationDAO.getLastLocation()


     // Insère un nouveau point de localisation dans la base de données.
     // Convertit l'objet Location standard en notre entité LocationPoint.
     // Utilise withContext(Dispatchers.IO) pour s'assurer que l'opération de base de données
     // s'exécute sur un thread d'arrière-plan dédié aux I/O.

    suspend fun insertLocation(location: Location) {
        val locationPoint = LocationPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = if (location.hasAltitude()) location.altitude else 0.0, // Gérer l'absence d'altitude
            accuracy = if (location.hasAccuracy()) location.accuracy else 0.0f, // Gérer l'absence de précision
            speed = if (location.hasSpeed()) location.speed else 0.0f,         // Gérer l'absence de vitesse
            timestamp = location.time // Utiliser le timestamp fourni par l'objet Location
        )
        withContext(Dispatchers.IO) { // Exécute l'insertion sur le thread IO
            locationDAO.insertLocation(locationPoint)
        }
    }

     // Supprime tous les points de localisation.

    suspend fun deleteAllLocations() {
        withContext(Dispatchers.IO) {
            locationDAO.deleteAllLocations()
        }
    }

    // Ajoutez d'autres méthodes si nécessaire, en déléguant au DAO et en utilisant withContext(Dispatchers.IO)
    // pour les opérations suspendues.
}

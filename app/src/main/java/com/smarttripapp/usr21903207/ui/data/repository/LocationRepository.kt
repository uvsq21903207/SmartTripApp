package com.smarttripapp.usr21903207.ui.data.repository

import android.location.Location
import com.smarttripapp.usr21903207.ui.data.database.LocationDao
import com.smarttripapp.usr21903207.ui.data.database.LocationPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository pour gérer les données de localisation.
 * Sert de médiateur entre les sources de données (ici, le DAO Room) et le reste de l'application (ViewModel, Service).
 * Permet d'abstraire la source de données et d'ajouter potentiellement de la logique métier
 * ou de combiner des données locales et distantes (non pertinent ici).
 *
 * @property locationDAO Le DAO injecté pour accéder à la base de données.
 */
class LocationRepository(private val locationDAO: LocationDao) {

    /**
     * Récupère tous les points de localisation sous forme de Flow.
     * Délègue simplement l'appel au DAO.
     */
    val allLocations: Flow<List<LocationPoint>> = locationDAO.getAllLocations()

    /**
     * Récupère le dernier point de localisation sous forme de Flow.
     * Délègue simplement l'appel au DAO.
     */
    val lastLocation: Flow<LocationPoint?> = locationDAO.getLastLocation()


    /**
     * Insère un nouveau point de localisation dans la base de données.
     * Convertit l'objet Location standard en notre entité LocationPoint.
     * Utilise withContext(Dispatchers.IO) pour s'assurer que l'opération de base de données
     * s'exécute sur un thread d'arrière-plan dédié aux I/O.
     *
     * @param location L'objet Location reçu du service de localisation.
     */
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

    /**
     * Supprime tous les points de localisation.
     * Utilise withContext(Dispatchers.IO).
     */
    suspend fun deleteAllLocations() {
        withContext(Dispatchers.IO) {
            locationDAO.deleteAllLocations()
        }
    }

    // Ajoutez d'autres méthodes si nécessaire, en déléguant au DAO et en utilisant withContext(Dispatchers.IO)
    // pour les opérations suspendues.
}

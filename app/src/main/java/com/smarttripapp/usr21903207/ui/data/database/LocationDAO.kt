package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) pour interagir avec la table "location_points".
 * Définit les opérations possibles sur la base de données pour les LocationPoint.
 * Room générera l'implémentation de cette interface.
 */
@Dao
interface LocationDao {

    /**
     * Insère un nouveau point de localisation dans la base de données.
     * Si un point avec le même ID existe déjà (peu probable avec autoGenerate=true),
     * il sera ignoré grâce à OnConflictStrategy.IGNORE.
     * La fonction est 'suspend' car elle doit être appelée depuis une coroutine ou une autre fonction suspend.
     * @param locationPoint Le point à insérer.
     * @return L'ID de la ligne insérée (Long).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(locationPoint: LocationPoint): Long // Renvoie l'ID inséré

    /**
     * Récupère tous les points de localisation enregistrés, triés par timestamp (du plus ancien au plus récent).
     * Renvoie un Flow, ce qui signifie que l'observateur sera notifié automatiquement
     * si les données de la table changent. C'est idéal pour l'affichage dans l'UI.
     * @return Un Flow contenant la liste de tous les LocationPoint.
     */
    @Query("SELECT * FROM location_points ORDER BY timestamp ASC")
    fun getAllLocations(): Flow<List<LocationPoint>>

    /**
     * Récupère tous les points de localisation enregistrés pour un voyage spécifique (étape future).
     * @param tripId L'ID du voyage.
     * @return Un Flow contenant la liste des LocationPoint pour ce voyage.
     */
    // @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    // fun getLocationsForTrip(tripId: Long): Flow<List<LocationPoint>> // Décommentez quand tripId sera ajouté

    /**
     * Supprime tous les points de localisation de la table.
     * Utile pour réinitialiser les données, par exemple.
     * La fonction est 'suspend'.
     */
    @Query("DELETE FROM location_points")
    suspend fun deleteAllLocations()

    /**
     * Récupère le dernier point de localisation enregistré (le plus récent).
     * Peut être utile pour afficher la dernière position connue rapidement.
     * Renvoie un Flow pour observer les changements.
     * @return Un Flow contenant le dernier LocationPoint ou null s'il n'y en a pas.
     */
    @Query("SELECT * FROM location_points ORDER BY timestamp DESC LIMIT 1")
    fun getLastLocation(): Flow<LocationPoint?>

    // Ajoutez d'autres méthodes de requête selon vos besoins (ex: supprimer un point spécifique,
    // récupérer des points dans une période donnée, etc.)
}

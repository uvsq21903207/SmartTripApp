package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


 // DAO (Data Access Object) pour interagir avec la table "location_points".
 // Définit les opérations possibles sur la base de données pour les LocationPoint.

@Dao
interface LocationDao {


     // Insère un nouveau point de localisation dans la base de données.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(locationPoint: LocationPoint): Long // Renvoie l'ID inséré


     // Récupère tous les points de localisation enregistrés, triés par timestamp (du plus ancien au plus récent).

    @Query("SELECT * FROM location_points ORDER BY timestamp ASC")
    fun getAllLocations(): Flow<List<LocationPoint>>


     // Supprime tous les points de localisation de la table.
    @Query("DELETE FROM location_points")
    suspend fun deleteAllLocations()


     // Récupère le dernier point de localisation enregistré (le plus récent).
    @Query("SELECT * FROM location_points ORDER BY timestamp DESC LIMIT 1")
    fun getLastLocation(): Flow<LocationPoint?>

}

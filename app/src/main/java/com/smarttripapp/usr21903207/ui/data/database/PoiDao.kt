package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow


@Dao
interface PoiDao { // Méthodes pour interagir avec la table "poi_points" dans la base de données


    @Insert(onConflict = OnConflictStrategy.REPLACE) // Crée un nouvel POI, si un POI avec le même ID existe déjà, il sera remplacé
    suspend fun insertPoi(poiPoint: PoiPoint): Long


    @Query("SELECT * FROM poi_points WHERE id = :id") // Récupère un POI spécifique par son ID
    suspend fun getPoiById(id: Long): PoiPoint?


    @Query("SELECT * FROM poi_points WHERE tripId = :tripId ORDER BY timestamp DESC") // Récupère tous les POIs d'un voyage spécifique ordonnés par timestamp décroissant
    fun getPoisByTripId(tripId: Long): Flow<List<PoiPoint>>


    @Query("SELECT * FROM poi_points ORDER BY timestamp DESC") // Récupère tous les POIs de la base de données ordonnés par timestamp décroissant
    fun getAllPois(): Flow<List<PoiPoint>>


    @Update
    suspend fun updatePoi(poiPoint: PoiPoint): Int // Met à jour un POI et retourne le nombre de lignes mises à jour


    @Delete
    suspend fun deletePoi(poiPoint: PoiPoint): Int // Supprime un POI et retourne le nombre de lignes supprimées


    @Query("DELETE FROM poi_points WHERE tripId = :tripId") //Supprime tous les POIs d'un voyage
    suspend fun deletePoisByTripId(tripId: Long)


    @Query("DELETE FROM poi_points") //Supprime tous les POIs de la table
    suspend fun deleteAllPois()
}
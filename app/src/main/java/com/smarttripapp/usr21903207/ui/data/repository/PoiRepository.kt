package com.smarttripapp.usr21903207.ui.data.repository

import com.smarttripapp.usr21903207.ui.data.database.PoiDao
import com.smarttripapp.usr21903207.ui.data.database.PoiPoint
import kotlinx.coroutines.flow.Flow

class PoiRepository(private val poiDao: PoiDao) {

    suspend fun insertPoi(poiPoint: PoiPoint) { // Insère un point d'intérêt dans la base de données
        poiDao.insertPoi(poiPoint)
    }

    suspend fun getPoiById(id: Long): PoiPoint? { // Récupère un point d'intérêt par son ID
        return poiDao.getPoiById(id)
    }

    fun getPoisByTripId(tripId: Long): Flow<List<PoiPoint>> { // Récupère tous les points d'intérêt d'un voyage spécifique
        return poiDao.getPoisByTripId(tripId)
    }

    fun getAllPois(): Flow<List<PoiPoint>> { // Récupère tous les points d'intérêt de la base de données
        return poiDao.getAllPois()
    }

    suspend fun updatePoi(poiPoint: PoiPoint) { // Met à jour un point d'intérêt dans la base de données
        poiDao.updatePoi(poiPoint)
    }

    suspend fun deletePoi(poiPoint: PoiPoint) { // Supprime un point d'intérêt de la base de données
        poiDao.deletePoi(poiPoint)
    }

    suspend fun deletePoisByTripId(tripId: Long) { // Supprime tous les points d'intérêt d'un voyage spécifique
        poiDao.deletePoisByTripId(tripId)
    }

    suspend fun deleteAllPois() { // Supprime tous les points d'intérêt de la base de données
        poiDao.deleteAllPois()
    }

}
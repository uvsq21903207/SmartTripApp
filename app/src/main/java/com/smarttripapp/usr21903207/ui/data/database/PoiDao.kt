package com.smarttripapp.usr21903207.ui.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoi(poi: PoiPoint)

    @Query("SELECT * FROM poi_points ORDER BY timestamp DESC")
    fun getAllPois(): Flow<List<PoiPoint>>
}

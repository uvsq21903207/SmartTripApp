package com.smarttripapp.usr21903207.ui.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [LocationPoint::class, PoiPoint::class], version = 1, exportSchema = false) // Définit les entités et la version de la base de données
abstract class AppDatabase : RoomDatabase() {


    abstract fun locationDao(): LocationDao // Méthode abstraite pour accéder au DAO des points de localisation

    abstract fun poiDao(): PoiDao // Méthode abstraite pour accéder au DAO des points d'intérêt

    companion object {
        // Volatile assure que la valeur de INSTANCE est toujours à jour et la même pour tous les threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase { // Fonction pour obtenir l'instance de la base de données
            // Si INSTANCE n'est pas null, la retourner.
            // Sinon, créer la base de données dans un bloc synchronisé .
            return INSTANCE ?: synchronized(this) { // Bloc synchronisé pour éviter les accès concurrents
                val instance = Room.databaseBuilder( // Construire la base de données Room
                    context.applicationContext, // Utiliser le contexte de l'application
                    AppDatabase::class.java,    // La classe de la base de données
                    "smarttrip_database"        // Nom du fichier de la base de données
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

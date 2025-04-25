package com.smarttripapp.usr21903207.ui.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Classe principale de la base de données Room pour l'application.
 * Elle définit les entités (tables) et fournit l'accès aux DAOs.
 * Utilise le pattern Singleton pour s'assurer qu'une seule instance de la base de données
 * est créée dans l'application.
 */
@Database(
    entities = [LocationPoint::class], // Liste des entités (tables) incluses dans la DB
    version = 1,                       // Version de la base de données (à incrémenter lors de changements de schéma)
    exportSchema = false               // Mettre à true si vous prévoyez des migrations de schéma complexes
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Fournit une instance du DAO pour interagir avec la table des points de localisation.
     * Room implémente cette méthode abstraite.
     */
    abstract fun locationDao(): LocationDao

    companion object {
        // Volatile assure que la valeur de INSTANCE est toujours à jour et la même pour tous les threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Récupère l'instance Singleton de la base de données.
         * Crée la base de données si elle n'existe pas encore.
         * @param context Le contexte de l'application.
         * @return L'instance unique de AppDatabase.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE n'est pas null, la retourner.
            // Sinon, créer la base de données dans un bloc synchronisé pour éviter les accès concurrents.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Utiliser le contexte de l'application
                    AppDatabase::class.java,    // La classe de la base de données
                    "smarttrip_database"        // Nom du fichier de la base de données
                )
                    // .fallbackToDestructiveMigration() // Optionnel: si vous ne gérez pas les migrations, supprime et recrée la DB en cas de changement de version. À éviter en production !
                    // .addMigrations(...) // Pour gérer les migrations de schéma proprement
                    .build()
                INSTANCE = instance
                // Retourner l'instance
                instance
            }
        }
    }
}

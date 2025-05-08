package com.smarttripapp.usr21903207.ui.service // Assurez-vous que le package est correct

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.smarttripapp.usr21903207.MainActivity
import com.smarttripapp.usr21903207.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.smarttripapp.usr21903207.ui.data.database.AppDatabase // Import de la classe Database
import com.smarttripapp.usr21903207.ui.data.repository.LocationRepository // Import du Repository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // Scope pour les coroutines du service
    private lateinit var locationClient: LocationClient // Client de localisation
    private lateinit var locationRepository: LocationRepository // Référence au Repository pour accéder à la base de données
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // Format pour afficher l'heure dans les logs et la notification

    // Binder non utilisé pour le moment, mais requis par la classe Service
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    // Initialisation du client de localisation lors de la création du service
    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient( // Initialisation du client de localisation
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        // Initialisation du Repository en obtenant le DAO depuis l'instance de la base de données
        val database = AppDatabase.getDatabase(applicationContext)
        locationRepository = LocationRepository(database.locationDao())

        createNotificationChannel() // Création le canal de notification
        Log.d("LocationService", "Service créé")
    }

    // Démarrage du service et des mises à jour de localisation
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        // START_STICKY: Si le service est tué par le système, il tentera de le redémarrer
        return START_STICKY
    }

    // Arrêt du service et annulation des coroutines
    override fun onDestroy() {
        super.onDestroy() // Appel de la méthode de la classe parente
        serviceScope.cancel() // Annule toutes les coroutines lors de la destruction lancées dans ce scope
        Log.d("LocationService", "Service détruit")
    }

    // Méthode pour démarrer le suivi et le service de premier plan
    private fun start() {
        Log.d("LocationService", "Tentative de démarrage du service...")
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Suivi SmartTrip")
            .setContentText("Localisation: N/A") // Sera mis à jour
            .setSmallIcon(R.drawable.ic_launcher_foreground) // icone à remplacer
            .setOngoing(true) // Notification non supprimable par balayage
            .setContentIntent(getMainActivityPendingIntent()) // Ouvre l'app au clic
            .build()

        // Démarrage du service en premier plan (requis pour le suivi en arrière-plan)
        // Le nombre (NOTIFICATION_ID) doit être > 0
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            Log.d("LocationService", "Service démarré en premier plan.")
        } catch (e: Exception) {
            Log.e("LocationService", "Erreur startForeground: ${e.message}")
            // Gérer l'erreur (par exemple, permission manquante POST_NOTIFICATIONS sur Android 13+)
            stopSelf() // Arrêter le service si startForeground échoue
            return
        }


        // Lancement de la collecte des mises à jour de localisation dans le scope du service
        locationClient
            .getLocationUpdates(LOCATION_UPDATE_INTERVAL_MS) // Intervalle de 10 secondes
            .catch { e ->
                // Gérer les erreurs (permissions, GPS désactivé...)
                Log.e("LocationService", "Erreur de localisation: ${e.message}")
                // Notifier l'utilisateur si le service s'est interrompu
            }
            .onEach { location ->
                // Traitement de chaque nouvelle localisation reçue
                val currentTime = timeFormat.format(Date(location.time)) // Formattage de l'heure
                val lat = String.format("%.5f", location.latitude) // Format avec 5 décimales coordonnées latitude
                val lon = String.format("%.5f", location.longitude) // Format avec 5 décimales coordonnées longitude
                val accuracy = String.format("%.1f", location.accuracy) // Format avec 1 décimale de l'accuracy
                Log.d("LocationService", "Nouvelle localisation: Lat=${location.latitude}, Lon=${location.longitude}")

                // INTÉGRATION BASE DE DONNÉES
                try {
                    // Appel au Repository pour insérer la localisation dans la DB
                    // Le Repository utilise withContext(Dispatchers.IO) pour exécuter sur le bon thread
                    locationRepository.insertLocation(location)
                    Log.d("LocationService", "Localisation insérée dans la base de données.")
                } catch (e: Exception) {
                    Log.e("LocationService", "Erreur lors de l'insertion en base de données: ${e.message}")
                    // Gérer l'erreur d'insertion si nécessaire
                }
                // FIN INTÉGRATION BASE DE DONNÉES

                // Mettre à jour la notification avec les nouvelles coordonnées
                val updatedNotification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Suivi SmartTrip Actif")
                    .setContentText("Dernier point @ $currentTime: $lat, $lon (±${accuracy}m)")
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // icône de l'appli dans les notifications
                    .setOngoing(true)
                    .setContentIntent(getMainActivityPendingIntent())
                    .build()

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)

            }
            .launchIn(serviceScope) // Lance la collecte dans le scope du service

        Log.d("LocationService", "Collecte des mises à jour de localisation lancée.") // Log de confirmation
    }

    // Méthode pour arrêter le service de premier plan et le suivi
    private fun stop() {
        Log.d("LocationService", "Arrêt du service demandé.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    // Crée le canal de notification (requis à partir d'Android 8.0 Oreo / API 26)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Vérifie la version d'Android
            val channel = NotificationChannel( // Création du canal
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Importance basse pour moins d'interruptions
            )
            channel.description = "Notifications pour le suivi de localisation SmartTrip"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // Récupération du service
            manager.createNotificationChannel(channel) // Création du canal de notification
            Log.d("LocationService", "Canal de notification créé.")
        }
    }

    // Crée un PendingIntent pour ouvrir MainActivity lorsque l'utilisateur clique sur la notification
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0, // requestCode
        Intent(this, MainActivity::class.java).also { // Ajout d'un Intent pour MainActivity
            it.action = Intent.ACTION_MAIN // Action principale
            it.addCategory(Intent.CATEGORY_LAUNCHER) // Ajout de catégories
            // FLAG_ACTIVITY_CLEAR_TOP: Si MainActivity est déjà ouverte, l'amène au premier plan
            // FLAG_ACTIVITY_SINGLE_TOP: Ne recrée pas MainActivity si elle est déjà au sommet de la pile
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        },
        // FLAG_IMMUTABLE est requis pour Android 12+
        // FLAG_UPDATE_CURRENT: si le PendingIntent existe déjà, met à jour ses extras
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    // Constantes pour le service
    companion object { // Constantes en dehors des méthodes
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        const val NOTIFICATION_CHANNEL_ID = "location_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Suivi de Localisation"
        const val NOTIFICATION_ID = 1 // Doit être > 0

        const val LOCATION_UPDATE_INTERVAL_MS = 30000L // Intervalle de mise à jour, ici 30 secondes
        // Augmenter pour économiser encore plus la batterie, on devrais peut-être faire pour que l'intervalle
        // de mise à jour soit dynamique en fonction du pourcentage de batterie
    }
}


// Ce fichier est un service Android conçu pour tourner en arrière-plan (même si l'application
// n'est pas visible) afin de collecter les coordonnées GPS de manière continue. Il orchestre
// le suivi en arrière-plan, gère la notification visible par l'utilisateur, utilise le
// LocationClient pour obtenir les données GPS via Flow, et est prêt à sauvegarder ces données
// dès que la base de données sera en place.
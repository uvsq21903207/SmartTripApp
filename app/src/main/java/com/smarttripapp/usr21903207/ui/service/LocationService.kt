package com.smarttripapp.usr21903207.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import com.google.android.gms.location.Priority
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.smarttripapp.usr21903207.R
import com.smarttripapp.usr21903207.MainActivity

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private val NOTIFICATION_CHANNEL_ID = "LocationServiceChannel"
    private val NOTIFICATION_ID = 123 // Un ID unique pour la notification

    companion object {
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60000)
            .setMinUpdateIntervalMillis(30000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("LocationService", "Nouvelle localisation: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> startLocationUpdates()
            ACTION_STOP_SERVICE -> stopLocationService()
        }
        // START_STICKY: Si le service est tué par le système, il tentera de le redémarrer.
        return START_STICKY
    }

    private fun startLocationUpdates() {
        createNotificationChannel() // Crée le canal pour Android 8 (Oreo) et supérieur
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification) // Démarre en mode avant-plan

        // Vérification des permissions avant de démarrer les mises à jour de localisation
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper() // le callback sera appelé sur le thread principal
            )
            Log.d("LocationService", "Demande de localisation démarrée")
        } catch (unlikely: SecurityException) {
            Log.e("LocationService", "Permissions de localisation manquantes.", unlikely)
            stopSelf() // Arrêter le service si les permissions manquent
        }
    }

    private fun stopLocationService() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("LocationService", "Mises à jour de localisation arrêtées")
        } catch (e: Exception) {
            Log.e("LocationService", "Erreur lors de l'arrêt des mises à jour", e)
        }
        stopForeground(STOP_FOREGROUND_REMOVE) // Updated to use the new API
        stopSelf() // Arrête le service
    }

    // Méthode pour créer la notification du service d'avant-plan
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java) // L'activité à ouvrir au clic
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val stopIntent = Intent(this, LocationService::class.java).apply { // Création de la notification avec l'action au clic et le bouton pour arrêter le service  
            action = ACTION_STOP_SERVICE
        }
        val stopServicePendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Suivi de voyage actif")
            .setContentText("Collecte des coordonnées GPS en cours...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Mettez votre propre icône !
            .setContentIntent(pendingIntent) // Action au clic sur la notif
            .addAction(R.drawable.ic_launcher_foreground, "Arrêter", stopServicePendingIntent) // Bouton pour arrêter le service de localisation
            .setOngoing(true) // Rend la notification non balayable
            .build()
    }

    // Nécessaire pour Android 8 (Oreo) et supérieur
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_LOW // LOW pour moins d'interruptions
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Assurez-vous que les mises à jour sont arrêtées si le service est détruit
        // removeLocationUpdates devrait déjà être appelé dans stopLocationService
        Log.d("LocationService", "Service détruit")
    }

    // onBind est nécessaire, mais retourne null si vous n'avez pas besoin de lier le service
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
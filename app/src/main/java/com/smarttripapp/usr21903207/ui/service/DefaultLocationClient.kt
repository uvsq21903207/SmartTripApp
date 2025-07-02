package com.smarttripapp.usr21903207.ui.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat


 //Implémentation par défaut de LocationClient utilisant FusedLocationProviderClient.

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient // Injecté depuis le service
): LocationClient {

    @SuppressLint("MissingPermission") // La permission est vérifiée avant l'appel
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        // callbackFlow est utilisé pour convertir les callbacks API en Flow Kotlin Coroutines
        return callbackFlow {
            // 1. Vérifier les permissions
            if (!hasLocationPermission()) {
                throw LocationClient.LocationException("Permissions de localisation manquantes (Fine ou Coarse)")
            }

            // 2. Vérifier si le GPS est activé
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("Le GPS est désactivé")
            }

            // 3. Configurer la requête de localisation
            // Priorité équilibrée pour économiser la batterie tout en ayant une bonne précision
            val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, interval)
                .setMinUpdateIntervalMillis(interval / 2) // Intervalle minimum
                .build()

            // 4. Définir le Callback pour recevoir les mises à jour
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    // Envoyer la dernière localisation reçue via le Flow
                    result.lastLocation?.let { location ->
                        // 'trySend' est utilisé dans un callbackFlow pour émettre des valeurs
                        // Il est non bloquant et gère la fermeture du Flow.
                        val sendResult = trySend(location)
                        if (sendResult.isFailure) {
                            // Gérer l'échec si nécessaire (par exemple, si le Flow est déjà fermé)
                            println("Échec de l'envoi de la localisation: ${sendResult.exceptionOrNull()?.message}")
                        }
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    super.onLocationAvailability(availability)
                    if (!availability.isLocationAvailable) {
                        println("Localisation non disponible actuellement.")
                    }
                }
            }

            // 5. Démarrer les mises à jour de localisation
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper() // Looper principal pour les callbacks UI-thread safe
            ).addOnFailureListener { e ->
                close(e) // Fermer le flow avec l'exception en cas d'échec du démarrage
            }

            // 6. Définir ce qui se passe quand le Flow est fermé (collecteur annulé)
            awaitClose {
                // Arrêter les mises à jour de localisation lorsque le Flow est annulé
                client.removeLocationUpdates(locationCallback)
                println("Mises à jour de localisation arrêtées.")
            }
        }
    }

//Vérifie si les permissions de localisation nécessaires sont accordées.
    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }
}

// Ce fichier est l'implémentation concrète de l'interface LocationClient
// Il utilise le FusedLocationProviderClient (l'outil fourni par Google Play Services)
// pour demander et recevoir les coordonnées GPS du téléphone
// Vérifie les permissions et si le GPS est activé.
// Il configure la fréquence et la précision des demandes GPS (LocationRequest)
// Il utilise callbackFlow pour transformer les appels "callback" de l'API Google
// en un Flow moderne et facile à utiliser par le LocationService.

//Role: Fournir les données de localisation réelles en utilisant les API Android/Google,
// tout en respectant le contrat défini par LocationClient
package com.smarttripapp.usr21903207.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission") // Nous vérifions les permissions manuellement ci-dessous
@Composable
fun CurrentLocationScreen(navController: NavController) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    // Un nouvel état pour stocker les messages d'erreur
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Ce bloc s'exécute une seule fois quand l'écran apparaît
    LaunchedEffect(Unit) {
        try {
            // On vérifie à nouveau la permission, par sécurité
            val hasPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                // On demande la dernière position connue
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc: Location? ->
                        if (loc != null) {
                            location = loc
                        } else {
                            // Cas où la localisation est activée mais aucune position n'est en cache
                            errorMessage = "Impossible d'obtenir la dernière position connue. Assurez-vous que le GPS est activé et réessayez."
                        }
                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        // En cas d'échec de l'appel
                        errorMessage = "Erreur lors de la récupération de la position: ${e.message}"
                        isLoading = false
                    }
            } else {
                errorMessage = "La permission de localisation n'a pas été accordée."
                isLoading = false
            }
        } catch (e: Exception) {
            // SÉCURITÉ ANTI-CRASH : Ce bloc va attraper toute autre erreur inattendue
            // (ex: services Google Play manquants) et l'afficher au lieu de faire planter l'app.
            errorMessage = "Une erreur inattendue est survenue: ${e.message}"
            isLoading = false
        }
    }

    // --- Interface Utilisateur ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            // Affiche une roue de chargement
            CircularProgressIndicator()
        } else if (location != null) {
            // Affiche la position si elle a été trouvée
            Text("Position Actuelle", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Latitude: ${location!!.latitude}", fontSize = 18.sp)
            Text("Longitude: ${location!!.longitude}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { showLocationOnMap(context, location) }) {
                Text("Afficher sur la carte")
            }
        } else {
            // Affiche le message d'erreur s'il y en a un
            Text(
                text = errorMessage ?: "Une erreur inconnue est survenue.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour revenir en arrière
        Button(onClick = { navController.popBackStack() }) {
            Text("Retour au menu")
        }
    }
}

/**
 * Fonction pour ouvrir une application de cartographie avec les coordonnées données.
 */
private fun showLocationOnMap(context: Context, location: Location?) {
    if (location == null) {
        Toast.makeText(context, "Coordonnées non disponibles", Toast.LENGTH_SHORT).show()
        return
    }

    val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(Ma Position Actuelle)")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        Toast.makeText(context, "Google Maps n'est pas installé.", Toast.LENGTH_SHORT).show()
    }
}

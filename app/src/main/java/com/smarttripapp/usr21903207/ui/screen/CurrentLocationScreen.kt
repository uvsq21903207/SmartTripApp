package com.smarttripapp.usr21903207.ui.screen

import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smarttripapp.usr21903207.ui.theme.AppTheme
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun CurrentLocationScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var address by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text( // Titre de l'écran
            "Position Actuelle",
            style = AppTheme.typography.headline,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text( // Affichage de la latitude
            "Latitude: $latitude",
            style = AppTheme.typography.body,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text( // Affichage de la longitude
            "Longitude: $longitude",
            style = AppTheme.typography.body,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text( // Affichage de l'adresse
            "Adresse: $address",
            style = AppTheme.typography.body,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Button( // Bouton pour mettre à jour la position actuelle
            onClick = {
                coroutineScope.launch { // Lancer une coroutine pour mettre à jour la position actuelle
                    isLoading = true // Démarquer le chargement
                    updateLocation(context) { lat, lon, addr -> // Mettre à jour la position actuelle
                        latitude = lat
                        longitude = lon
                        address = addr
                        isLoading = false // Arrêter le chargement
                    }
                }
            },
            modifier = Modifier.padding(bottom = 16.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Chargement..." else "Actualiser la position")
        }
        
        Button(onClick = { navController.navigateUp() }) { // Naviguer vers l'écran précédent
            Text("Retour au menu")
        }
    }
}

// Fonction pour mettre à jour la position actuelle
suspend fun updateLocation(context: Context, onLocationUpdated: (Double, Double, String) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try { // Vérifier si la permission de localisation est accordée
        // Si la permission est accordée, obtenir la dernière position connue et mettre à jour la position actuelle
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Obtenir la dernière position connue
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { // Mettre à jour la position actuelle
                    val address = getAddressFromLocation(context, it)
                    // Appeler la fonction de rappel avec la nouvelle position et l'adresse
                    onLocationUpdated(it.latitude, it.longitude, address)
                }
            }
        } else { // Si la permission n'est pas accordée, afficher un message d'erreur
            onLocationUpdated(0.0, 0.0, "Permission de localisation non accordée")
        }
    } catch (e: Exception) { // En cas d'erreur, afficher un message d'erreur
        onLocationUpdated(0.0, 0.0, "Erreur: ${e.localizedMessage}")
    }
}

// Fonction pour obtenir l'adresse à partir de la position
fun getAddressFromLocation(context: Context, location: Location): String {
    // Utiliser Geocoder pour obtenir l'adresse à partir de la position
    val geocoder = Geocoder(context, Locale.getDefault())
    // Utiliser la méthode getFromLocation pour obtenir les adresses correspondantes à la position
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return if (addresses?.isNotEmpty() == true) { // Si des adresses sont trouvées, retourner l'adresse formatée
        addresses[0].getAddressLine(0) ?: "Adresse non trouvée"
    } else { // Sinon, retourner un message d'erreur
        "Adresse non trouvée"
    }
}
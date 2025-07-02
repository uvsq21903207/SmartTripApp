package com.smarttripapp.usr21903207.ui.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smarttripapp.usr21903207.ui.service.LocationService
import com.smarttripapp.usr21903207.ui.theme.AppTheme

@Composable
fun MenuScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Un état simple pour savoir si le service est en cours
    var isServiceRunning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menu Principal", style = AppTheme.typography.headline)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Suivi GPS", style = AppTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Le bouton n'a plus besoin de vérifier les permissions ici
        Button(
            onClick = {
                startLocationService(context)
                isServiceRunning = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isServiceRunning
        ) {
            Text("Démarrer le Suivi GPS")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                stopLocationService(context)
                isServiceRunning = false
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isServiceRunning
        ) {
            Text("Arrêter le Suivi GPS")
        }
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        Text("Fonctionnalités", style = AppTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(AppDestinations.ADD_POI_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Ajouter un Point d'Intérêt (POI)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(AppDestinations.MAP_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir la Carte (Maps SDK)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(AppDestinations.CURRENT_LOCATION_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir ma Position Actuelle")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(AppDestinations.TRIP_RECAP_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Retracer le Voyage")
        }
    }
}

private fun startLocationService(context: Context) {
    val startIntent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
    }
    context.startService(startIntent)
    Toast.makeText(context, "Suivi démarré.", Toast.LENGTH_SHORT).show()
}

private fun stopLocationService(context: Context) {
    val stopIntent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
    }
    context.startService(stopIntent)
    Toast.makeText(context, "Suivi arrêté.", Toast.LENGTH_SHORT).show()
}

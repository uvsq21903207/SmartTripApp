package com.smarttripapp.usr21903207.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Define route constants for better management
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ADD_POI_SCREEN = "addPoi"
    const val MAP_SCREEN = "map"
    const val CURRENT_LOCATION_SCREEN = "currentLocation"
    const val TRIP_RECAP_SCREEN = "tripRecap"
    // Add other destinations as needed
}


@Composable
fun MenuScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // Add some padding around the content
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menu Principal", style = AppTheme.typography.headline) // Use custom typography if available
        Spacer(modifier = Modifier.height(32.dp)) // Increase spacing

        // Button to Add POI
        Button(onClick = {
            // Navigate to the screen for adding POIs (placeholder action)
            navController.navigate(AppDestinations.ADD_POI_SCREEN)
            // Log.d("MenuScreen", "Navigate to Add POI screen") // Example logging
        }, modifier = Modifier.fillMaxWidth()) { // Make button wider
            Text("Ajouter un Point d'Intérêt (POI)")
        }
        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

        // Button to View Map
        Button(onClick = {
            // Navigate to the map screen (placeholder action)
            navController.navigate(AppDestinations.MAP_SCREEN)
            // Log.d("MenuScreen", "Navigate to Map screen") // Example logging
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir la Carte (Maps SDK)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to View Current Location
        Button(onClick = {
            // Navigate to the current location screen (placeholder action)
            navController.navigate(AppDestinations.CURRENT_LOCATION_SCREEN)
            // Log.d("MenuScreen", "Navigate to Current Location screen") // Example logging
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir ma Position Actuelle")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to Reconstruct Trip
        Button(onClick = {
            // Navigate to the trip reconstruction screen (placeholder action)
            navController.navigate(AppDestinations.TRIP_RECAP_SCREEN)
            // Log.d("MenuScreen", "Navigate to Trip Recap screen") // Example logging
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Retracer le Voyage")
        }
    }
}
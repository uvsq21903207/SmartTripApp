package com.smarttripapp.usr21903207.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smarttripapp.usr21903207.ui.theme.AppTheme

@Composable
fun TripRecapScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Récapitulatif du Voyage",
            style = AppTheme.typography.headline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Placeholder for trip details
        Text(
            "Détails du voyage ici",
            style = AppTheme.typography.body,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { /* TODO: Implement trip data refresh */ },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Actualiser les données")
        }

        Button(onClick = { navController.navigateUp() }) {
            Text("Retour au menu")
        }
    }
}
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
fun MapScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Carte",
            style = AppTheme.typography.headline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Placeholder for map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(bottom = 16.dp)
        ) {
            Text("Ici sera affichée la carte")
        }

        Button(onClick = { navController.navigateUp() }) {
            Text("Retour au menu")
        }
    }
}
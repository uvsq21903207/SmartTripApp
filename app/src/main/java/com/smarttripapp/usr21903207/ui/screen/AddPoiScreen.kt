package com.smarttripapp.usr21903207.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smarttripapp.usr21903207.ui.theme.AppTheme

@Composable
fun AddPoiScreen(navController: NavController, modifier: Modifier = Modifier) {
    var poiName by remember { mutableStateOf("") }
    var poiDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Ajouter un Point d'Intérêt",
            style = AppTheme.typography.headline,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = poiName,
            onValueChange = { poiName = it },
            label = { Text("Nom du POI") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = poiDescription,
            onValueChange = { poiDescription = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { /* TODO: Implement POI saving logic */ },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Sauvegarder le POI")
        }

        Button(onClick = { navController.navigateUp() }) {
            Text("Retour au menu")
        }
    }
}
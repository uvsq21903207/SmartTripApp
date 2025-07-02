package com.smarttripapp.usr21903207.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun PermissionsScreen(navController: NavController) {
    val context = LocalContext.current

    // Liste complète de toutes les permissions dont l'application a besoin
    val requiredPermissions = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    // Un état pour savoir si toutes les permissions sont accordées
    var allPermissionsGranted by remember {
        mutableStateOf(areAllPermissionsGranted(context, requiredPermissions))
    }

    // Lanceur pour demander les permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            // Après la réponse de l'utilisateur, on revérifie
            allPermissionsGranted = areAllPermissionsGranted(context, requiredPermissions)
        }
    )

    // Si toutes les permissions sont déjà accordées, on navigue directement au menu
    if (allPermissionsGranted) {
        // `LaunchedEffect` est utilisé pour s'assurer que la navigation ne se produit
        // qu'une seule fois et pas à chaque recomposition.
        LaunchedEffect(Unit) {
            navController.navigate(AppDestinations.MENU_SCREEN) {
                // On efface l'écran des permissions de l'historique de navigation
                popUpTo(AppDestinations.PERMISSIONS_SCREEN) { inclusive = true }
            }
        }
    } else {
        // Sinon, on affiche l'écran d'explication
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Autorisations requises",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Pour fonctionner correctement, SmartTrip a besoin d'accéder à votre position (même en arrière-plan) et d'afficher des notifications.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { permissionLauncher.launch(requiredPermissions.toTypedArray()) }) {
                Text("Accorder les autorisations")
            }
        }
    }
}

// Fonction utilitaire pour vérifier si une liste de permissions est accordée
private fun areAllPermissionsGranted(context: Context, permissions: List<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

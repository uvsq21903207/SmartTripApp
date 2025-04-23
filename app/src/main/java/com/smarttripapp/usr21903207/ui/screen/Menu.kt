package com.smarttripapp.usr21903207.ui.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider // Ne pas effacer c'est un import pour la ligne de séparation
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttripapp.usr21903207.ui.service.LocationService
import com.smarttripapp.usr21903207.ui.theme.AppTheme
import com.smarttripapp.usr21903207.ui.screen.CurrentLocationScreen

// Définition des destinations de navigation
object AppDestinations {
    const val MENU_SCREEN = "menu"
    const val ADD_POI_SCREEN = "addPoi"
    const val MAP_SCREEN = "map"
    const val CURRENT_LOCATION_SCREEN = "currentLocation"
    const val TRIP_RECAP_SCREEN = "tripRecap"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.MENU_SCREEN) {
        composable(AppDestinations.MENU_SCREEN) {
            MenuScreen(navController = navController)
        }
        composable(AppDestinations.ADD_POI_SCREEN) {
            // Implement this screen later
            Text("Ajouter POI")
        }
        composable(AppDestinations.MAP_SCREEN) {
            // Implement this screen later
            Text("Carte SDK")
        }
        composable(AppDestinations.CURRENT_LOCATION_SCREEN) {
            CurrentLocationScreen(navController = navController)
        }
        composable(AppDestinations.TRIP_RECAP_SCREEN) {
            // Implement this screen later
            Text("Retracer Voyage")
        }
    }
}

@Composable
fun MenuScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current // Obtenir le contexte
    // Liste des permissions requises (ajustée pour SDK 35)
    val locationPermissions = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            // Ajouter les permissions supplémentaires nécessaires pour SDK 35+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            // FOREGROUND_SERVICE_LOCATION est nécessaire pour foregroundServiceType="location" sur API 34+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
            }
            // POST_NOTIFICATIONS est nécessaire pour les notifs sur API 33+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toList() // Convertir en liste immuable
    }
// État pour savoir si les permissions sont accordées (vérification initiale)
    var hasPermissions by remember {
        mutableStateOf(checkInitialPermissions(context, locationPermissions))
    }

    // État pour savoir si le service est en cours d'exécution
    var isServiceRunning by remember { mutableStateOf(false) }

    // Le launcher pour demander les permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Re-vérifier si toutes les permissions nécessaires sont maintenant accordées
            hasPermissions = checkInitialPermissions(context, locationPermissions)

            if (hasPermissions) {
                // Permissions accordées : on peut démarrer le service
                startLocationService(context)
                isServiceRunning = true
                Toast.makeText(context, "Permissions accordées, suivi démarré.", Toast.LENGTH_SHORT).show()
            } else {
                // Sinon, afficher un message d'erreur
                Toast.makeText(context, "Certaines permissions requises ont été refusées. Le suivi ne peut pas démarrer correctement.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // --- Interface Utilisateur ---

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre principal avec un style personnalisé donné dans Typography
        Text("Menu Principal", style = AppTheme.typography.headline)
        Spacer(modifier = Modifier.height(32.dp))

        // --- Section Suivi GPS ---
        Text("Suivi GPS", style = AppTheme.typography.titleMedium) // Titre de section
        Spacer(modifier = Modifier.height(8.dp))

        // Bouton Démarrer le Suivi
        Button(
            onClick = {
                if (checkInitialPermissions(context, locationPermissions)) {
                    startLocationService(context)
                    isServiceRunning = true
                    Toast.makeText(context, "Suivi démarré.", Toast.LENGTH_SHORT).show()
                } else {
                    permissionLauncher.launch(locationPermissions.toTypedArray())
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isServiceRunning
        ) {
            Text("Démarrer le Suivi GPS")
        }
        Spacer(modifier = Modifier.height(8.dp)) // Espacement réduit

        // Bouton Arrêter le Suivi
        Button(
            onClick = {
                stopLocationService(context)
                isServiceRunning = false
                Toast.makeText(context, "Suivi arrêté.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isServiceRunning
        ) {
            Text("Arrêter le Suivi GPS")
        }
        Spacer(modifier = Modifier.height(24.dp)) // Espacement avant la prochaine section
        HorizontalDivider() // Ligne de séparation
        Spacer(modifier = Modifier.height(24.dp)) // Espacement après la séparation

        // --- Section Fonctionnalités ---
        Text("Fonctionnalités", style = AppTheme.typography.titleMedium) // Titre de section
        Spacer(modifier = Modifier.height(16.dp))


        // Bouton Ajouter POI
        Button(onClick = {
            navController.navigate(AppDestinations.ADD_POI_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Ajouter un Point d'Intérêt (POI)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Voir Carte
        Button(onClick = {
            navController.navigate(AppDestinations.MAP_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir la Carte (Maps SDK)")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Voir Position Actuelle
        Button(onClick = {
            navController.navigate(AppDestinations.CURRENT_LOCATION_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Voir ma Position Actuelle")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Retracer Voyage
        Button(onClick = {
            navController.navigate(AppDestinations.TRIP_RECAP_SCREEN)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Retracer le Voyage")
        }
    }
}

// --- Fonctions utilitaires (placées hors du composable, dans le même fichier) ---

// Vérifie si les permissions essentielles sont déjà accordées mais ne vérifie pas si l'utilisateur a révoqué les droits entretemps
private fun checkInitialPermissions(context: Context, permissions: List<String>): Boolean {
    // Vérifie si toutes les permissions de la liste sont accordées
    val allGranted = permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
    return allGranted
}

// Fonction pour démarrer le service de localisation
private fun startLocationService(context: Context) {
    val startIntent = Intent(context, LocationService::class.java)
    startIntent.action = LocationService.ACTION_START_SERVICE
    ContextCompat.startForegroundService(context, startIntent)
}

// Fonction pour arrêter le service de localisation
private fun stopLocationService(context: Context) {
    val stopIntent = Intent(context, LocationService::class.java)
    stopIntent.action = LocationService.ACTION_STOP_SERVICE
    context.startService(stopIntent)
}
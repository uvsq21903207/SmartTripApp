package com.smarttripapp.usr21903207

import android.Manifest
import android.content.Intent
import android.os.Build // Import pour la vérification de version Android
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttripapp.usr21903207.ui.screen.AppDestinations // Import destinations
import com.smarttripapp.usr21903207.ui.data.database.AppDatabase
import com.smarttripapp.usr21903207.ui.data.repository.LocationRepository
import com.smarttripapp.usr21903207.ui.data.repository.PoiRepository // Import du PoiRepository
import com.smarttripapp.usr21903207.ui.screen.AddPoiScreen
import com.smarttripapp.usr21903207.ui.screen.CurrentLocationScreen
import com.smarttripapp.usr21903207.ui.screen.MenuScreen
import com.smarttripapp.usr21903207.ui.screen.MapScreen
import com.smarttripapp.usr21903207.ui.screen.TripRecapScreen
import com.smarttripapp.usr21903207.ui.service.LocationService
import com.smarttripapp.usr21903207.ui.theme.AppTheme // Use your actual theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Demande des permissions nécessaires
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        // Ajoute la permission POST_NOTIFICATIONS si sur Android 13 (Tiramisu, API 33) ou plus
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppDestinations.MENU_SCREEN) {
        // Define the Menu screen
        composable(AppDestinations.MENU_SCREEN) {
            MenuScreen(navController = navController)
        }
        composable(AppDestinations.ADD_POI_SCREEN) {
            androidx.compose.material3.Text("Ajouter POI")
        }
        composable(AppDestinations.MAP_SCREEN) {
            MapScreen(navController = navController)
        }
        composable(AppDestinations.CURRENT_LOCATION_SCREEN) {
            CurrentLocationScreen(navController = navController)
        }
        composable(AppDestinations.TRIP_RECAP_SCREEN) {
            androidx.compose.material3.Text("Retracer Voyage")
        }
    }
}
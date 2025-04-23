package com.smarttripapp.usr21903207

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttripapp.usr21903207.ui.screen.AppDestinations // Import destinations
import com.smarttripapp.usr21903207.ui.screen.MenuScreen
import com.smarttripapp.usr21903207.ui.theme.AppTheme // Use your actual theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
            androidx.compose.material3.Text("Carte SDK")
        }
        composable(AppDestinations.CURRENT_LOCATION_SCREEN) {
            androidx.compose.material3.Text("Position Actuelle")
        }
        composable(AppDestinations.TRIP_RECAP_SCREEN) {
            androidx.compose.material3.Text("Retracer Voyage")
        }
    }
}
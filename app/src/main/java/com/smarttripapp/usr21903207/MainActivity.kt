package com.smarttripapp.usr21903207

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttripapp.usr21903207.ui.screen.*
import com.smarttripapp.usr21903207.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Le NavHost gère la navigation entre les écrans
                    val navController = rememberNavController()
                    // On démarre sur l'écran des permissions
                    NavHost(navController = navController, startDestination = AppDestinations.PERMISSIONS_SCREEN) {
                        composable(AppDestinations.PERMISSIONS_SCREEN) {
                            PermissionsScreen(navController = navController)
                        }
                        composable(AppDestinations.MENU_SCREEN) {
                            MenuScreen(navController = navController)
                        }
                        composable(AppDestinations.ADD_POI_SCREEN) {
                            AddPoiScreen(navController = navController)
                        }
                        composable(AppDestinations.MAP_SCREEN) {
                            MapScreen(navController = navController)
                        }
                        composable(AppDestinations.CURRENT_LOCATION_SCREEN) {
                            CurrentLocationScreen(navController = navController)
                        }
                        composable(AppDestinations.TRIP_RECAP_SCREEN) {
                            // Placeholder
                        }
                    }
                }
            }
        }
    }
}

// Fichier : app/src/main/java/com/smarttripapp/usr21903207/ui/screen/MapScreen.kt

package com.smarttripapp.usr21903207.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    // Un état pour savoir si la carte est prête à être affichée.
    // C'est la clé pour éviter les crashs.
    var isMapReady by remember { mutableStateOf(false) }

    // Position par défaut (par exemple, Paris).
    // La caméra se positionnera ici au début.
    val defaultLocation = LatLng(48.8566, 2.3522)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    // Propriétés de la carte (par exemple, type de carte normal).
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = com.google.maps.android.compose.MapType.NORMAL))
    }

    // Paramètres de l'interface utilisateur de la carte (activation du zoom, etc.).
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Le composable GoogleMap qui affiche la carte.
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
                // Ce callback est appelé quand la carte a fini de se charger.
                onMapLoaded = {
                    isMapReady = true
                }
            )

            // Affiche une roue de chargement TANT QUE la carte n'est pas prête.
            // Cela empêche l'application de crasher en attendant que la carte s'initialise.
            if (!isMapReady) {
                CircularProgressIndicator()
            }
        }
    }
}

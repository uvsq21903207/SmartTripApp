package com.smarttripapp.usr21903207.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.smarttripapp.usr21903207.ui.data.database.AppDatabase
import com.smarttripapp.usr21903207.ui.data.database.PoiPoint
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission") // Permissions are checked before use
@Composable
fun AddPoiScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.poiDao() }

    val pois by dao.getAllPois().collectAsState(initial = emptyList())

    var poiName by remember { mutableStateOf("") }
    var poiType by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Ajouter un Point d'Intérêt", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = poiName,
            onValueChange = { poiName = it },
            label = { Text("Nom du lieu (ex: Tour Eiffel)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = poiType,
            onValueChange = { poiType = it },
            label = { Text("Type (ex: Monument, Restaurant...)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (poiName.isBlank() || poiType.isBlank()) {
                    Toast.makeText(context, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                // Step 1: Check for Google Play Services
                val googleApiAvailability = GoogleApiAvailability.getInstance()
                if (googleApiAvailability.isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
                    isLoading = false
                    Toast.makeText(context, "Les services Google Play ne sont pas disponibles.", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Step 2: Check for location permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    isLoading = false
                    Toast.makeText(context, "Permission de localisation non accordée.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Step 3: Try to get current location
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                val newPoi = PoiPoint(
                                    name = poiName,
                                    type = poiType,
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                                coroutineScope.launch {
                                    try {
                                        dao.insertPoi(newPoi)
                                        poiName = ""
                                        poiType = ""
                                        Toast.makeText(context, "POI ajouté !", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erreur de base de données: ${e.message}", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Position non trouvée. Activez le GPS et réessayez.", Toast.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(context, "Erreur lors de la récupération de la position: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(context, "Une erreur inattendue est survenue: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Ajouter le POI à ma position actuelle")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text("Lieux Enregistrés", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (pois.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucun point d'intérêt enregistré.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(pois) { poi ->
                    PoiListItem(poi)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PoiListItem(poi: PoiPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = poi.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Type: ${poi.type}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Coordonnées: ${String.format("%.4f", poi.latitude)}, ${String.format("%.4f", poi.longitude)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

package com.smarttripapp.usr21903207.ui.service

import android.location.Location
import kotlinx.coroutines.flow.Flow


 // Interface pour abstraire la récupération des mises à jour de localisation.
 // Utilise Flow pour émettre les mises à jour de manière asynchrone.
interface LocationClient {

     //Récupère les mises à jour de localisation.
    fun getLocationUpdates(interval: Long): Flow<Location>


    // Classe d'exception personnalisée pour les erreurs liées à la localisation.
    class LocationException(message: String): Exception(message)
}

// Ce fichier permet de déclarer une interface pour la récupération des mises à jour de localisation.
// En gros, cette interface dit simplement : "Si tu veux être un LocationClient,
// tu dois avoir une fonction getLocationUpdates qui renvoie un Flow d'objets Location."
//Il définit aussi une exception spécifique (LocationException) pour les problèmes liés à
// la localisation.
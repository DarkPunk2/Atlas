package com.project.atlas.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.atlas.models.Location
import kotlinx.coroutines.tasks.await

class LocationRepository {
    val db = Firebase.firestore

    fun addLocation(location: Location){
        val dbLocation = hashMapOf(
            "lat" to location.lat,
            "lon" to location.lon,
            "alias" to location.alias
        )

        db.collection("locations")
            .add(dbLocation)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    suspend fun getAllLocations(): List<Location> {
        val locationsList = mutableListOf<Location>()
        try {
            val result: QuerySnapshot = db.collection("locations")
                .get()
                .await()

            if (!result.isEmpty) {
                for (document in result) {
                    val newLocation: Location = document.toObject(Location::class.java)
                    locationsList.add(newLocation)
                }
                Log.d(TAG, "Locations retrieved from database")
                Log.d(TAG, "Locations size repository: ${locationsList.size}")
            } else {
                Log.d(TAG, "No results found in database")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting documents.", e)
        }
        return locationsList
    }
}
package com.project.atlas.services

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.atlas.models.Location
import com.project.atlas.models.UserModel
import kotlinx.coroutines.tasks.await

class LocationRepository {
    val db = Firebase.firestore

    fun addLocation(location: Location){
        val dbLocation = hashMapOf(
            "lat" to location.lat,
            "lon" to location.lon,
            "alias" to location.alias
        )

        db.collection("users")
            .document(UserModel.eMail)
            .collection("locations")
            .document(location.alias)
            .set(dbLocation)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${location.alias}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    suspend fun getAllLocations(): List<Location> {
        val locationsList = mutableListOf<Location>()
        try {
            val result: QuerySnapshot = db.collection("users")
                .document(UserModel.eMail)
                .collection("locations")
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

    fun deleteLocation(location: Location){
        db.collection("users")
            .document(UserModel.eMail)
            .collection("locations")
            .document(location.alias)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}
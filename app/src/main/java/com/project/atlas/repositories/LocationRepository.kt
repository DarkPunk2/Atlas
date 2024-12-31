package com.project.atlas.repositories

import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.atlas.interfaces.LocationRepositoryInterface
import com.project.atlas.models.Location
import com.project.atlas.models.UserModel
import kotlinx.coroutines.tasks.await

class LocationRepository : LocationRepositoryInterface {
    val db = Firebase.firestore

    override fun addLocation(location: Location){
        val dbLocation = hashMapOf(
            "lat" to location.lat,
            "lon" to location.lon,
            "alias" to location.alias,
            "toponym" to location.toponym,
            "favourite" to location.isFavourite
        )

        db.collection("users")
            .document(UserModel.eMail)
            .collection("locations")
            .document(location.toponym)
            .set(dbLocation)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${location.alias}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    override suspend fun getAllLocations(): List<Location> {
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
                Log.d("Firestore", "Locations retrieved from database")
                Log.d("Firestore", "Locations size repository: ${locationsList.size}")
            } else {
                Log.d("Firestore", "No results found in database")
            }
        } catch (e: Exception) {
            Log.w("Firestore", "Error getting documents.", e)
        }
        return locationsList
    }

    override fun deleteLocation(location: Location){
        db.collection("users")
            .document(UserModel.eMail)
            .collection("locations")
            .document(location.toponym)
            .delete()
            .addOnSuccessListener { Log.d("Firestore", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Firestore", "Error deleting document", e) }
    }

    override fun updateLocation(location: Location, lat: Double, lon: Double, alias: String, toponym: String, favourite: Boolean) {
        val dbLocation = hashMapOf(
            "lat" to lat,
            "lon" to lon,
            "alias" to alias,
            "toponym" to toponym,
            "favourite" to favourite
        )

        db.collection("users")
            .document(UserModel.eMail)
            .collection("locations")
            .document(location.toponym)
            .update(dbLocation as Map<String, Any>)
            .addOnSuccessListener {
                Log.d("Firestore", "Ubicación actualizada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al actualizar la ubicación", e)
            }
    }

    override fun setFavourite(location: Location, value: Boolean) {
        updateLocation(location, location.lat, location.lon, location.alias, location.toponym, value)
    }
}
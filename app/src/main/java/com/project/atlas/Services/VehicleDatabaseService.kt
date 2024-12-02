package com.project.atlas.Services

import Diesel
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import com.project.atlas.Interfaces.EnergyType
import com.project.atlas.Interfaces.Petrol95
import com.project.atlas.Interfaces.VehicleInterface

import com.project.atlas.Models.VehicleModel
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VehicleDatabaseService : VehicleInterface {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var usersCollection:String = "users"
    fun setTestMode(){
        usersCollection = "usersTest"
    }
    override suspend fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        if (checkForDuplicates(user, vehicle)) {
            return false
        }

        val dbVehicle = hashMapOf(
            "alias" to vehicle.alias,
            "type" to vehicle.type,
            "energyType" to energyTypeToString(vehicle.energyType),
            "consumption" to vehicle.consumption
        )

        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicle.alias!!)
                .set(dbVehicle)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding vehicle: ${exception.message}")
                    continuation.resume(false)
                }
        }
    }
    override suspend fun listVehicle(user: String): List<VehicleModel>? {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .get()
                .addOnSuccessListener { result ->
                    val vehicleList = result.mapNotNull { document ->
                        document.toVehicle()
                    }
                    continuation.resume(vehicleList)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching vehicles: ${exception.message}")
                    continuation.resume(emptyList())
                }
        }
    }

    fun DocumentSnapshot.toVehicle(): VehicleModel {
        return VehicleModel(
            alias = getString("alias"),
            type = getString("type"),
            energyType = stringToEnergyType(getString("energyType")),
            consumption = getDouble("consumption")
        )
    }

    private suspend fun checkForDuplicates(user: String, vehicle: VehicleModel): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicle.alias!!)
                .get()
                .addOnSuccessListener { document ->
                    continuation.resume(document.exists())
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error checking duplicates: ${exception.message}")
                    continuation.resume(false)
                }
        }
    }

    private fun stringToEnergyType(value: String?): EnergyType? {
        return when (value) {
            "Petrol95" -> Petrol95()
            "Petrol98" -> Petrol98()
            "Diesel" -> Diesel()
            else -> null
        }
    }

    private fun energyTypeToString(energyType: EnergyType?): String? {
        return energyType?.typeName
    }


}

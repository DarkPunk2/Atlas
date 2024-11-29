package com.project.atlas.Services

import Diesel
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import com.project.atlas.Interfaces.EnergyType
import com.project.atlas.Interfaces.Petrol95
import com.project.atlas.Interfaces.VehicleInterface

import com.project.atlas.Models.VehicleModel
import java.util.concurrent.CountDownLatch

class VehicleDatabaseService : VehicleInterface {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        var success = false
        val latch = CountDownLatch(1)

        if (checkForDuplicates(user, vehicle)) {
            return false
        }

        val dbVehicle = hashMapOf(
            "alias" to vehicle.alias,
            "type" to vehicle.type,
            "energyType" to energyTypeToString(vehicle.energyType),
            "consumption" to vehicle.consumption
        )

        db.collection("users")
            .document(user)
            .collection("vehicles")
            .document(vehicle.alias!!)
            .set(dbVehicle)
            .addOnSuccessListener {
                success = true
                latch.countDown()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error adding vehicle: ${exception.message}")
                latch.countDown()
            }
        latch.await()
        return success
    }


    override fun listVehicle(user: String): List<VehicleModel>? {
        val vehicleList = mutableListOf<VehicleModel>()
        val latch = CountDownLatch(1) // Para bloquear hasta que Firebase complete la operación
        var success = false

        db.collection("users")
            .document(user)
            .collection("vehicles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val vehicle = document.toVehicle()
                    if (vehicle != null) {
                        vehicleList.add(vehicle)
                    }
                }
                success = true
                latch.countDown()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching vehicles: ${exception.message}")
                latch.countDown() // Libera el bloqueo incluso si ocurre un error
            }

        latch.await()

        return if (success) {
            vehicleList
        } else {
            println("Error en la consulta")
            emptyList()
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

    private fun checkForDuplicates(user: String, vehicle: VehicleModel): Boolean {
        var exists = false
        val latch = CountDownLatch(1)

        db.collection("users")
            .document(user)
            .collection("vehicles")
            .document(vehicle.alias!!)
            .get()
            .addOnSuccessListener { document ->
                exists = document.exists()
                latch.countDown()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error checking duplicates: ${exception.message}")
                latch.countDown()
            }
        latch.await()
        return exists
    }


}

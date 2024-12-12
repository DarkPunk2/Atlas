package com.project.atlas.services

import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.VehicleNotExistsException

import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface

import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VehicleDatabaseService : VehicleInterface {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var usersCollection:String = "users"
    fun setTestMode(){
        usersCollection = "usersTest"
    }
    fun vehicleToHashMap (vehicle: VehicleModel): HashMap<String, Serializable> {
        val dbVehicle = hashMapOf(
        "alias" to vehicle.alias as Serializable,
        "type" to vehicle.type as Serializable,
        "energyType" to energyTypeToString(vehicle.energyType) as Serializable,
        "consumption" to vehicle.consumption as Serializable
        )
        return dbVehicle
    }

    public suspend fun createDefaults(user: String) : Boolean {
        val vWalk = VehicleModel(VehicleType.Walk.toString(), VehicleType.Walk, Calories(), 60.0)
        val vCycle = VehicleModel(VehicleType.Cycle.toString(), VehicleType.Cycle, Calories(), 30.0)
        val dbWalk = vehicleToHashMap(vWalk)
        val dbCycle = vehicleToHashMap(vCycle)

        return suspendCoroutine<Boolean> { continuation ->
            val walkTask = db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vWalk.alias!!)
                .set(dbWalk)

            val cycleTask = db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vCycle.alias!!)
                .set(dbCycle)

            val tasks = listOf(walkTask, cycleTask)
            val allTasks = tasks.map { task ->
                task.addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding vehicle: ${exception.message}")
                    continuation.resume(false)
                }
            }
            walkTask.addOnSuccessListener {
                cycleTask.addOnSuccessListener {
                    continuation.resume(true)
                }
            }
        }
    }

    override suspend fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        if (!vehicle.alias.isNullOrBlank() and checkForDuplicates(user, vehicle.alias!!)) {
            return false
        }
        val dbVehicle = vehicleToHashMap(vehicle)
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
    override suspend fun listVehicle(user: String): List<VehicleModel>{
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

    override suspend fun deleteVehicle(user: String, vehicleAlias: String): Boolean {
        if(!checkForDuplicates(user, vehicleAlias)) throw VehicleNotExistsException("El vehículo ${vehicleAlias} no existe en la base de datos")
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicleAlias).delete() //el método delete siempre se considera existoso
                .addOnSuccessListener {
                    continuation.resume(true)
                }
        }
    }

    override suspend fun updateVehicle(user: String, vehicleAlias: String, vehicle: VehicleModel): Boolean {
        if(getVehicle(user, vehicleAlias).equals(vehicle)){ // si son iguales no hay por qué actualizar, si no existe salta excepción
            return false
        }
        if (vehicleAlias.equals(vehicle.alias)){
            return updateExistentVehicle(user,vehicle)
        }else{
            return updateAliasAsWell(user,vehicleAlias, vehicle)
        }
    }

    override suspend fun getVehicle(user: String, vehicleAlias: String): VehicleModel {
        if (!checkForDuplicates(user, vehicleAlias)) throw VehicleNotExistsException("El vehículo ${vehicleAlias} no existe")
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles").
                document(vehicleAlias).get()
                .addOnSuccessListener { document ->
                    val vehicle = document.toVehicle()
                    continuation.resume(vehicle)
                }
        }
    }

    private suspend fun updateExistentVehicle(user:String, vehicle:VehicleModel):Boolean{
        val dbVehicle = vehicleToHashMap(vehicle)
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicle.alias!!).set(dbVehicle)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching vehicles: ${exception.message}")
                    continuation.resume(false)
                }
        }
    }
    private suspend fun updateAliasAsWell(user:String, vehicleOldAlias: String, vehicleUpdated: VehicleModel):Boolean{
        if(deleteVehicle(user, vehicleOldAlias)){
            return addVehicle(user, vehicleUpdated)
        }
        return false
    }

    fun DocumentSnapshot.toVehicle(): VehicleModel {
        return VehicleModel(
            alias = getString("alias"),
            type = stringToVehicleType(getString("type"))!!,
            energyType = stringToEnergyType(getString("energyType")),
            consumption = getDouble("consumption")
        )
    }

    private suspend fun checkForDuplicates(user: String, vehicleAlias: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicleAlias)
                .get()
                .addOnSuccessListener { document ->
                    continuation.resume(document.exists())
                }
        }
    }
    public suspend fun checkForVehicles(user: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .get() // Recuperamos todos los documentos de la colección "vehicles"
                .addOnSuccessListener { querySnapshot ->
                    // Si la colección tiene documentos, devolvemos true
                    continuation.resume(querySnapshot.isEmpty.not())
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error checking vehicles: ${exception.message}")
                    continuation.resume(false)
                }
        }
    }

    private fun stringToEnergyType(value: String?): EnergyType? {
        return when (value) {
            "Petrol 95" -> Petrol95()
            "Petrol 98" -> Petrol98()
            "Diesel" -> Diesel()
            "Electricity" -> Electricity()
            "Calories" -> Calories()
            else -> null
        }
    }
    private fun stringToVehicleType(value: String?): VehicleType? {
        return when (value) {
            "Car" -> VehicleType.Car
            "Bike" -> VehicleType.Bike
            "Scooter" -> VehicleType.Scooter
            "Walk" -> VehicleType.Walk
            "Cycle" -> VehicleType.Cycle
            else -> null
        }
    }

    private fun energyTypeToString(energyType: EnergyType?): String? {
        return energyType?.typeName
    }


}

package com.project.atlas.services

import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.project.atlas.exceptions.VehicleNotExistsException

import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface

import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VehicleDatabaseService : VehicleInterface {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val a = db.enableNetwork()
    private var usersCollection:String = "users"

    fun vehicleToHashMap (vehicle: VehicleModel): HashMap<String, Serializable> {
        val dbVehicle = hashMapOf(
        "alias" to vehicle.alias as Serializable,
        "type" to vehicle.type as Serializable,
        "energyType" to energyTypeToString(vehicle.energyType) as Serializable,
        "consumption" to vehicle.consumption as Serializable,
        "favourite" to vehicle.favourite as Serializable
        )
        return dbVehicle
    }

    override fun observeVehicles(user: String): Flow<List<VehicleModel>> = callbackFlow {
        val listener = db.collection(usersCollection)
            .document(user)
            .collection("vehicles")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val vehicles = snapshots?.documents?.mapNotNull { document ->
                    document.toVehicle()
                }.orEmpty()

                trySend(vehicles).isSuccess
            }

        awaitClose{ listener.remove() }
    }


    override suspend fun createDefaults(user: String) : Boolean {
        val vWalk = VehicleModel(VehicleType.Walk.toString(), VehicleType.Walk, Calories(), 3.8)
        val vCycle = VehicleModel(VehicleType.Cycle.toString(), VehicleType.Cycle, Calories(), 7.0)
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
        if (vehicle.alias.isNullOrBlank()) {
            return false
        }
        val dbVehicle = vehicleToHashMap(vehicle)

        // Usar suspendCoroutine para que la función sea suspensiva
        return suspendCoroutine { continuation ->
            // Establecer el alias como el ID del documento
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .document(vehicle.alias!!)  // Alias como ID del documento
                .set(dbVehicle)
                .addOnSuccessListener {
                    // Si se ejecuta correctamente, continuar con true
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    // Si falla la operación, no fallar inmediatamente
                    // Dejar que Firestore maneje la cola en caso de no conexión
                    Log.e("Firebase", "Error adding vehicle: ${exception.message}")
                    // Aquí no se llama a continuation.resume(false) inmediatamente,
                    // ya que Firestore intentará realizar la operación cuando haya conexión
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
                    //continuation.resume(false)
                }
        }
    }
    private suspend fun updateAliasAsWell(user:String, vehicleOldAlias: String, vehicleUpdated: VehicleModel):Boolean{
        var defaultAlias = getDefaultVehicle(user)?.alias
        updateVehicleReferenceInRoutes(user, vehicleOldAlias, vehicleUpdated)
        if(deleteVehicle(user, vehicleOldAlias) and addVehicle(user, vehicleUpdated)){
            if(defaultAlias == vehicleOldAlias) { setDefaultVehicle(user, vehicleUpdated) }
            return true
        }
        return false
    }

    private suspend fun updateVehicleReferenceInRoutes(user: String, vehicleOldAlias: String, vehicleUpdated: VehicleModel) {
        // Aquí creamos las referencias completas para los vehículos
        val oldVehicleRef = db.collection("users")
            .document(user)
            .collection("vehicles")
            .document(vehicleOldAlias)  // Referencia del vehículo viejo

        val newVehicleRef = db.collection("users")
            .document(user)
            .collection("vehicles")
            .document(vehicleUpdated.alias!!)  // Referencia del vehículo nuevo

        // Buscamos las rutas que tienen el vehículo con el alias viejo
        val routesQuery = db.collection("users")
            .document(user)
            .collection("routes")
            .whereEqualTo("vehicleRef", oldVehicleRef)
            .get()

        routesQuery.addOnSuccessListener { routesSnapshot ->
            for (routeDoc in routesSnapshot) {
                val routeRef = routeDoc.reference

                // Actualizamos la referencia del vehículo en la ruta con la nueva referencia
                routeRef.update("vehicleRef", newVehicleRef)
                    .addOnSuccessListener {
                        Log.d("VehicleService", "Route reference updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("VehicleService", "Error updating route reference: ${e.message}")
                    }
            }
        }
    }

    fun DocumentSnapshot.toVehicle(): VehicleModel {
        return VehicleModel(
            alias = getString("alias"),
            type = stringToVehicleType(getString("type"))!!,
            energyType = stringToEnergyType(getString("energyType")),
            consumption = getDouble("consumption"),
            favourite = getBoolean("favourite")!!
        )
    }

    override suspend fun checkForDuplicates(user: String, vehicleAlias: String): Boolean {
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

    override suspend fun deleteAll(user: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection("vehicles")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tasks = mutableListOf<Task<Void>>()
                    for (document in querySnapshot.documents) {
                        tasks.add(document.reference.delete())
                    }
                    Tasks.whenAllComplete(tasks).addOnSuccessListener {
                        continuation.resume(true)
                    }.addOnFailureListener { exception ->
                        continuation.resume(false)
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(false)
                }
        }
    }

    override suspend fun checkForVehicles(user: String): Boolean {
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

    override suspend fun setDefaultVehicle(user: String, vehicle: VehicleModel): Boolean {
        val vehicleRef = db.collection("users")
            .document(user)
            .collection("vehicles")
            .document(vehicle.alias!!)

        val defaultVehicleDoc = db.collection("users")
            .document(user)
            .collection("defaultVehicle")
            .document("current")

        defaultVehicleDoc.set(mapOf("vehicleRef" to vehicleRef))
            .addOnSuccessListener {
                Log.d("VehicleService", "Vehicle successfully set as default")
            }
            .addOnFailureListener { exception ->
                Log.e("VehicleService", "Error setting default vehicle: ${exception.message}")
            }
        return true
    }



    override suspend fun getDefaultVehicle(user: String): VehicleModel? {
        return suspendCoroutine { continuation ->
            val defaultVehicleDocRef = db.collection(usersCollection)
                .document(user)
                .collection("defaultVehicle")
                .document("current")

            // Intentar obtener el documento de "defaultVehicle"
            defaultVehicleDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // El documento existe, intentar obtener la referencia al vehículo
                        val vehicleRef = document.getDocumentReference("vehicleRef")
                        if (vehicleRef != null) {
                            // Si la referencia existe, obtener el vehículo asociado
                            vehicleRef.get()
                                .addOnSuccessListener { vehicleDoc ->
                                    if (vehicleDoc.exists()) {
                                        // Convertir el documento a un modelo de vehículo
                                        val vehicle = vehicleDoc.toVehicle()  // Asumiendo que toVehicle convierte correctamente
                                        continuation.resume(vehicle)
                                    } else {
                                        // Si el documento del vehículo no existe, retornar null
                                        Log.d("Firestore", "Vehicle document does not exist.")
                                        continuation.resume(null)
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // En caso de error al recuperar el vehículo
                                    Log.e("Firestore", "Error fetching vehicle data: ${exception.message}")
                                    continuation.resume(null)
                                }
                        } else {
                            // Si no hay referencia al vehículo, retornar null
                            Log.d("Firestore", "No vehicle reference found in defaultVehicle document.")
                            continuation.resume(null)
                        }
                    } else {
                        // Si el documento de defaultVehicle no existe, retornar null
                        Log.d("Firestore", "Default vehicle document does not exist.")
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    // En caso de error al obtener el documento de defaultVehicle
                    Log.e("Firestore", "Error fetching default vehicle document: ${exception.message}")
                    continuation.resume(null)
                }
        }
    }

    override suspend fun deleteDefaultVehicle(user: String): Boolean {
        return suspendCoroutine { continuation ->
            val collectionRef = db.collection(usersCollection)
                .document(user)
                .collection("defaultVehicle")

            collectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val batch = db.batch()
                    for (document in querySnapshot.documents) {
                        batch.delete(document.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            continuation.resume(true)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Error deleting default vehicle collection: ${exception.message}")
                            continuation.resume(false)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching default vehicle collection: ${exception.message}")
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

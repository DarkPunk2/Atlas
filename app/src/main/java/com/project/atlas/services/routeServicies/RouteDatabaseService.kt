package com.project.atlas.services.routeServicies


import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RouteDatabaseService: RouteDatabase {
    private val db = FirebaseFirestore.getInstance()

    private var usersCollection:String = "users"
    private val collectionId: String = "routes"

    fun setTestMode(){
        usersCollection = "usersTest"
    }

    override suspend fun add(route: RouteModel): Boolean {
        val dbRute = routeToMap(route)

        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .document(route.id)
                .set(dbRute)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding rute: ${exception.message}")
                    continuation.resumeWithException(ServiceNotAvailableException(exception.message ?: "Service not available"))
                }
        }
    }

    override suspend fun remove(routeID: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .document(routeID).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding rute: ${exception.message}")
                    continuation.resumeWithException(ServiceNotAvailableException(exception.message ?: "Service not available"))
                }
        }
    }

    private fun routeToMap(route: RouteModel): Map<String, Any> {
        val vehicleRef = db.collection("users")
            .document(UserModel.eMail)
            .collection("vehicles")
            .document(route.vehicle.alias!!)

        return mapOf(
            "id" to route.id,
            "start" to mapOf(
                "lat" to route.start.lat,
                "lon" to route.start.lon,
                "alias" to route.start.alias,
                "toponym" to route.end.toponym
            ),
            "end" to mapOf(
                "lat" to route.end.lat,
                "lon" to route.end.lon,
                "alias" to route.end.alias,
                "toponym" to route.end.toponym
            ),
            /*"vehicle" to mapOf(
                "alias" to route.vehicle.alias,
                "type" to route.vehicle.type.name,
                "energyType" to route.vehicle.energyType?.let { energyType ->
                    mapOf(
                        "typeName" to energyType.typeName,
                        "magnitude" to energyType.magnitude
                    )
                },
                "consumption" to route.vehicle.consumption
            )*/"vehicleRef" to vehicleRef,
            "ruteType" to route.routeType.name,
            "distance" to route.distance,
            "duration" to route.duration,
            "rute" to route.rute,
            "bbox" to route.bbox,
            "isFavorite" to route.isFavorite

        )
    }


    suspend fun getVehicleByReference(vehicleRef: DocumentReference): VehicleModel? {
        return suspendCoroutine { continuation ->
            db.document(vehicleRef.path).get() // Usar db para obtener el documento por su ruta
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        continuation.resume(document.toVehicle())
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching vehicle: ${exception.message}")
                    continuation.resume(null)
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


    private suspend fun DocumentSnapshot.toRuteModel(): RouteModel? {
        val start = get("start") as Map<String, Any>
        val end = get("end") as Map<String, Any>
        val vehicleRef = getDocumentReference("vehicleRef")

        // Si la referencia del vehículo es nula, elimina el documento y retorna null
        if (vehicleRef == null) {
            // Elimina el documento de la ruta ya que no tiene vehículo asociado
            db.collection("routes").document(id).delete().await() // Usar await para asegurarse de que la eliminación se complete
            return null
        }

        // Si la referencia del vehículo no es nula, obtenemos el vehículo asociado
        val vehicle = getVehicleByReference(vehicleRef)

        // Si el vehículo es nulo, elimina el documento
        if (vehicle == null) {
            // Elimina el documento de la ruta ya que no tiene un vehículo válido asociado
            withContext(Dispatchers.IO) {
                db.collection("routes").document(id).delete().await()
            }
            return null
        }

        // Si el vehículo existe, crea el modelo de ruta
        return RouteModel(
            id = getString("id")!!,
            start = Location(
                lat = start["lat"] as Double,
                lon = start["lon"] as Double,
                alias = start["alias"] as String,
                toponym = start["toponym"] as String
            ),
            end = Location(
                lat = end["lat"] as Double,
                lon = end["lon"] as Double,
                alias = end["alias"] as String,
                toponym = end["toponym"] as String
            ),
            vehicleReference = vehicleRef.id, // Guardar el ID de la referencia
            vehicle = vehicle, // Objeto completo del vehículo
            routeType = RouteType.valueOf(getString("ruteType")!!),
            distance = getDouble("distance")!!,
            duration = getDouble("duration")!!,
            rute = getString("rute")!!,
            bbox = get("bbox") as List<Double>,
            isFavorite = getBoolean("isFavorite") ?: false
        )
    }

     override suspend fun deleteInvalidRoutes() {
        try {
            // Obtener las rutas de Firestore
            val routesSnapshot = db.collection("routes").get().await()

            // Iterar sobre todas las rutas y verificar si el vehículo es válido
            for (document in routesSnapshot.documents) {
                // Obtener el ID de la referencia del vehículo
                val vehicleRefId = document.getString("vehicleRef") // O el nombre adecuado si es diferente

                // Si no hay referencia al vehículo, eliminamos la ruta
                if (vehicleRefId == null) {
                    remove(document.id) // Usar remove para eliminar la ruta
                } else {
                    // Verificar si el vehículo existe en la base de datos
                    val vehicleRef = db.collection("vehicles").document(vehicleRefId)
                    val vehicle = getVehicleByReference(vehicleRef)

                    // Si el vehículo no existe, eliminamos la ruta
                    if (vehicle == null) {
                        remove(document.id) // Usar remove para eliminar la ruta
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting invalid routes: ${e.message}")
        }
    }



    override suspend fun getAll(): List<RouteModel> {
        return withContext(Dispatchers.IO) {
            val result = db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .get()
                .await() // Usa await para obtener el resultado de forma asincrónica

            result.documents.mapNotNull { document ->
                runCatching { document.toRuteModel() }.getOrNull() // Manejo seguro para evitar excepciones
            }
        }
    }


    override suspend fun checkForDuplicates(user: String, id: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(user)
                .collection(collectionId)
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    continuation.resume(document.exists())
                }
        }
    }

    override suspend fun addDefaultRouteType(routeType: RouteType): Boolean {
        val routeTypeDB = hashMapOf("routeType" to routeType)
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .set(routeTypeDB)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error adding rute: ${exception.message}")
                    continuation.resumeWithException(ServiceNotAvailableException(exception.message ?: "Service not available"))
                }
        }
    }

    override suspend fun getDefaultRouteType(): RouteType {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .get()
                .addOnSuccessListener { result ->
                    val routeTypeString = result.getString("routeType")
                    val routeType = routeTypeString?.let {
                        try {
                            RouteType.valueOf(it)
                        }catch (e: IllegalArgumentException){
                            null
                        }
                    }
                    if (routeType != null) {
                        continuation.resume(routeType)
                    } else {
                        continuation.resumeWithException(NoSuchElementException("No RouteType found"))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching route type: ${exception.message}")
                    continuation.resumeWithException(ServiceNotAvailableException(exception.message ?: "Service not available"))
                }
        }
    }

    override suspend fun update(route: RouteModel): Boolean {
        val dbRute = routeToMap(route)

        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .document(route.id)
                .set(dbRute)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error updating route: ${exception.message}")
                    continuation.resumeWithException(ServiceNotAvailableException(exception.message ?: "Service not available"))
                }
        }
    }

}
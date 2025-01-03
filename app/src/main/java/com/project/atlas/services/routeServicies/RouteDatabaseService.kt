package com.project.atlas.services.routeServicies


import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
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
            "vehicle" to mapOf(
                "alias" to route.vehicle.alias,
                "type" to route.vehicle.type.name,
                "energyType" to route.vehicle.energyType?.let { energyType ->
                    mapOf(
                        "typeName" to energyType.typeName,
                        "magnitude" to energyType.magnitude
                    )
                },
                "consumption" to route.vehicle.consumption
            ),
            "ruteType" to route.routeType.name,
            "distance" to route.distance,
            "duration" to route.duration,
            "rute" to route.rute,
            "bbox" to route.bbox,
            "isFavorite" to route.isFavorite

        )
    }

    private fun DocumentSnapshot.toRuteModel(): RouteModel {
        val start = get("start") as Map<String, Any>
        val end = get("end") as Map<String, Any>
        val vehicle = get("vehicle") as Map<String, Any>
        val energyTypeMap = vehicle["energyType"] as? Map<String, Any>

        val energyType = energyTypeMap?.let {
            when (it["typeName"]) {
                    "Petrol 95" -> Petrol95()
                    "Petrol 98" -> Petrol98()
                    "Diesel" -> Diesel()
                    "Electricity" -> Electricity()
                    "Calories" -> Calories()
                    else -> null
                }
        }

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
                toponym = start["toponym"] as String
            ),
            vehicle = VehicleModel(
                alias = vehicle["alias"] as String?,
                type = VehicleType.valueOf(vehicle["type"] as String),
                energyType = energyType,
                consumption = vehicle["consumption"] as Double?
            ),
            routeType = RouteType.valueOf(getString("ruteType")!!),
            distance = getDouble("distance")!!,
            duration = getDouble("duration")!!,
            rute = getString("rute")!!,
            bbox = get("bbox") as List<Double>,
            isFavorite = getBoolean("isFavorite") ?: false
        )
    }




    override suspend fun getAll(): List<RouteModel> {
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .get()
                .addOnSuccessListener { result ->
                    val ruteList = result.mapNotNull { document ->
                        document.toRuteModel()
                    }
                    continuation.resume(ruteList)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase", "Error fetching vehicles: ${exception.message}")
                    continuation.resume(emptyList())
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
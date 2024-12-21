package com.project.atlas.services


import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.RouteNotFoundException
import com.project.atlas.exceptions.VehicleNotExistsException
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RouteDatabaseService: RouteDatabase {
    private val db = FirebaseFirestore.getInstance()

    private var usersCollection:String = "users"
    private val collectionId: String = "routes"

    fun setTestMode(){
        usersCollection = "usersTest"
    }

    override suspend fun add(route: RouteModel): Boolean {
        if (checkForDuplicates(UserModel.eMail, route.id)) {
            return false
        }
        val dbRute = ruteToMap(route)

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
                    continuation.resume(false)
                }
        }
    }

    override suspend fun remove(routeID: String): Boolean {
        if(!checkForDuplicates(UserModel.eMail, routeID)) throw RouteNotFoundException("La ruta ${routeID} no existe en la base de datos")
        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .document(routeID).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
        }
    }

    private fun ruteToMap(rute: RouteModel): Map<String, Any> {
        return mapOf(
            "id" to rute.id,
            "start" to mapOf(
                "lat" to rute.start.lat,
                "lon" to rute.start.lon,
                "alias" to rute.start.alias
            ),
            "end" to mapOf(
                "lat" to rute.end.lat,
                "lon" to rute.end.lon,
                "alias" to rute.end.alias
            ),
            "vehicle" to mapOf(
                "alias" to rute.vehicle.alias,
                "type" to rute.vehicle.type.name,
                "energyType" to rute.vehicle.energyType?.let { energyType ->
                    mapOf(
                        "typeName" to energyType.typeName,
                        "magnitude" to energyType.magnitude
                    )
                },
                "consumption" to rute.vehicle.consumption
            ),
            "ruteType" to rute.routeType.name,
            "distance" to rute.distance,
            "duration" to rute.duration,
            "rute" to rute.rute,
            "bbox" to rute.bbox
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
                alias = start["alias"] as String
            ),
            end = Location(
                lat = end["lat"] as Double,
                lon = end["lon"] as Double,
                alias = end["alias"] as String
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
            bbox = get("bbox") as List<Double>
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

    private suspend fun checkForDuplicates(user: String, id: String): Boolean {
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

}
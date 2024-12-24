package com.project.atlas.services


import Calories
import Diesel
import Electricity
import Petrol98
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.RuteDatabase
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RuteDatabaseService: RuteDatabase {
    private val db = FirebaseFirestore.getInstance()

    private var usersCollection:String = "users"
    private val collectionId: String = "routes"

    fun setTestMode(){
        usersCollection = "usersTest"
    }

    override suspend fun add(rute: RuteModel): Boolean {
        if (checkForDuplicates(UserModel.eMail, rute.id)) {
            return false
        }
        val dbRute = ruteToMap(rute)

        return suspendCoroutine { continuation ->
            db.collection(usersCollection)
                .document(UserModel.eMail)
                .collection(collectionId)
                .document(rute.id)
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

    private fun ruteToMap(rute: RuteModel): Map<String, Any> {
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
            "ruteType" to rute.ruteType.name,
            "distance" to rute.distance,
            "duration" to rute.duration,
            "rute" to rute.rute
        )
    }

    private fun DocumentSnapshot.toRuteModel(): RuteModel {
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

        return RuteModel(
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
            ruteType = RuteType.valueOf(getString("ruteType")!!),
            distance = getDouble("distance")!!,
            duration = getDouble("duration")!!,
            rute = getString("rute")!!
        )
    }




    override suspend fun getAll(): List<RuteModel> {
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

    override fun remove(): Boolean {
        TODO("Not yet implemented")
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
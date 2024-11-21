package com.project.atlas.Services

import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.Interfaces.VehicleInterface
import com.project.atlas.Models.VehicleModel

class VehicleDatabaseService : VehicleInterface {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        return false
    }

    override fun listVehicle(user: String): List<VehicleModel>? {
        return null;
    }
}

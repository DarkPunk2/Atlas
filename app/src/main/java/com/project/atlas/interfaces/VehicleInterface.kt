package com.project.atlas.interfaces

import com.project.atlas.models.VehicleModel

interface VehicleInterface {

    suspend fun addVehicle(user: String, vehicle: VehicleModel):Boolean
    suspend fun listVehicle(user:String): List<VehicleModel>?
}
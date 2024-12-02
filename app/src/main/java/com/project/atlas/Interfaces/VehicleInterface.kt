package com.project.atlas.Interfaces

import com.project.atlas.Models.VehicleModel

interface VehicleInterface {

    suspend fun addVehicle(user: String, vehicle: VehicleModel):Boolean
    suspend fun listVehicle(user:String): List<VehicleModel>?
}
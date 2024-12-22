package com.project.atlas.interfaces

import com.project.atlas.models.VehicleModel

interface VehicleInterface {

    suspend fun addVehicle(user: String, vehicle: VehicleModel):Boolean
    suspend fun listVehicle(user:String): List<VehicleModel>?
    suspend fun deleteVehicle(user:String, vehicleAlias:String):Boolean
    suspend fun updateVehicle(user: String, vehicleAlias:String, vehicle: VehicleModel): Boolean
    suspend fun getVehicle(user: String, vehicleAlias:String):VehicleModel
    suspend fun checkForDuplicates(user: String, vehicleAlias: String): Boolean
}
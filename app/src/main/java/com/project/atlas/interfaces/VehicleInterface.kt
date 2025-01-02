package com.project.atlas.interfaces

import com.project.atlas.models.VehicleModel
import kotlinx.coroutines.flow.Flow

interface VehicleInterface {

    suspend fun addVehicle(user: String, vehicle: VehicleModel):Boolean
    suspend fun listVehicle(user:String): List<VehicleModel>?
    suspend fun deleteVehicle(user:String, vehicleAlias:String):Boolean
    suspend fun updateVehicle(user: String, vehicleAlias:String, vehicle: VehicleModel): Boolean
    suspend fun getVehicle(user: String, vehicleAlias:String):VehicleModel
    suspend fun checkForDuplicates(user: String, vehicleAlias: String): Boolean
    suspend fun deleteAll(user: String) : Boolean
    suspend fun createDefaults(user: String) : Boolean
    suspend fun checkForVehicles(user: String) : Boolean
    suspend fun setDefaultVehicle(user: String, vehicle: VehicleModel) : Boolean
    suspend fun getDefaultVehicle(user: String) : VehicleModel?
    suspend fun deleteDefaultVehicle(user: String) : Boolean
    fun observeVehicles(user: String): Flow<List<VehicleModel>>
}
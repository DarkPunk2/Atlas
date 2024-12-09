package com.project.atlas.services

import Calories
import Diesel
import Electricity
import Petrol98
import android.annotation.SuppressLint
import com.project.atlas.exceptions.VehicleWrongBusinessRulesException
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType


class VehicleService(private val dbService: VehicleDatabaseService) : VehicleInterface {

    override suspend fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        checkBusinessRules(vehicle)
        return dbService.addVehicle(user, vehicle)
    }

    override suspend fun listVehicle(user: String): List<VehicleModel>? {
        return dbService.listVehicle(user)
    }
    override suspend fun deleteVehicle(user: String, vehicleAlias:String): Boolean {
        return dbService.deleteVehicle(user,vehicleAlias)
    }

    override suspend fun updateVehicle(user: String, vehicleAlias: String, vehicle: VehicleModel): Boolean {
        checkBusinessRules(vehicle)
        return dbService.updateVehicle(user, vehicleAlias, vehicle)
    }

    override suspend fun getVehicle(user: String, vehicleAlias: String): VehicleModel {
        val vehicle = dbService.getVehicle(user, vehicleAlias)
        checkBusinessRules(vehicle)
        return vehicle
    }

    @SuppressLint("SuspiciousIndentation")
    @Throws(VehicleWrongBusinessRulesException::class)
    fun checkBusinessRules(vehicle: VehicleModel){
        if (checkAlias(vehicle.alias) && checkConsumption(vehicle.consumption) && checkTypeWithEnergyType(vehicle)){
            return
        }
        throw VehicleWrongBusinessRulesException("Vehículo no válido")
    }

    private fun checkConsumption(consumption: Double?): Boolean {
            if (consumption != null){
                return consumption > 0.0
            }
            return false
    }

    private fun checkAlias(alias: String?): Boolean {
        return !alias.isNullOrBlank()
    }

    private fun checkTypeWithEnergyType(vehicle: VehicleModel):Boolean{
        //comprueba que el tipo coincida con la energía introducida
        when (vehicle.type) {
            VehicleType.Coche -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.Moto -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.Patinete -> return vehicle.energyType is Electricity
            VehicleType.Bicicleta -> return vehicle.energyType is Calories
            VehicleType.Andar -> return vehicle.energyType is Calories
            else -> {
                return false
            }
        }
    }

    private fun checkBusinessMotor(energyType: EnergyType?): Boolean {
        when (energyType){
            is Petrol95 -> return true
            is Petrol98 -> return true
            is Diesel -> return true
            is Electricity -> return true
            else -> {
                return false
            }
        }
    }
}
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
        if(!checkForVehicles(user)) createDefaults(user)
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

    override suspend fun checkForDuplicates(user: String, vehicleAlias: String): Boolean {
        return dbService.checkForDuplicates(user, vehicleAlias)
    }

    override suspend fun deleteAll(user: String): Boolean {
        return dbService.deleteAll(user)
    }

    override suspend fun createDefaults(user: String): Boolean {
        return dbService.createDefaults(user)
    }

    override suspend fun checkForVehicles(user:String): Boolean {
    return dbService.checkForVehicles(user)
}
    @SuppressLint("SuspiciousIndentation")
    @Throws(VehicleWrongBusinessRulesException::class)
    fun checkBusinessRules(vehicle: VehicleModel){
        if (checkAlias(vehicle.alias)){
            if(checkConsumption(vehicle.consumption)){
                if(checkTypeWithEnergyType(vehicle)){
                    return
                }else{
                    throw VehicleWrongBusinessRulesException("Energy type no válido")
                }
            }else{
                throw VehicleWrongBusinessRulesException("Consumption no válido")
            }
        }else{
            throw VehicleWrongBusinessRulesException("Alias no válido")
        }
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
            VehicleType.Car -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.Bike -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.Scooter -> return vehicle.energyType is Electricity
            VehicleType.Cycle -> return vehicle.energyType is Calories
            VehicleType.Walk -> return vehicle.energyType is Calories
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
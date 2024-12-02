package com.project.atlas.Services;

import Calories
import Electricity
import Petrol98
import android.annotation.SuppressLint
import com.project.atlas.Exceptions.VehicleWrongBusinessRulesException;
import com.project.atlas.Interfaces.EnergyType
import com.project.atlas.Interfaces.Petrol95
import com.project.atlas.Interfaces.VehicleInterface;
import com.project.atlas.Models.VehicleModel;
import com.project.atlas.Models.VehicleType


class VehicleService(private val dbService: VehicleDatabaseService) : VehicleInterface {

    override suspend fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        return dbService.addVehicle(user, vehicle)
    }

    override suspend fun listVehicle(user: String): List<VehicleModel>? {
        return dbService.listVehicle(user)
    }

    @SuppressLint("SuspiciousIndentation")
    @Throws(VehicleWrongBusinessRulesException::class)
    fun checkBusinessRules(vehicle: VehicleModel): Boolean {
        if (checkAlias(vehicle.alias) && checkConsumption(vehicle.consumption) && checkTypeWithEnergyType(vehicle)){
            return true
        }else
        throw VehicleWrongBusinessRulesException("Vehículo no válido")
        return false
    }

    private fun checkConsumption(consumption: Double?): Boolean {
            if (consumption != null){
                return consumption > 0.0
            }
            return false
    }

    private fun checkAlias(alias: String?): Boolean {
        return alias.isNullOrBlank()
    }

    private fun checkTypeWithEnergyType(vehicle: VehicleModel):Boolean{
        //comprueba que el tipo coincida con la energía introducida
        when (VehicleType.valueOf(vehicle.type!!)) {
            VehicleType.coche -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.moto -> return checkBusinessMotor(vehicle.energyType)
            VehicleType.patinete -> return vehicle.energyType is Electricity
            VehicleType.bicicleta -> return vehicle.energyType is Calories
            VehicleType.andar -> return vehicle.energyType is Calories
            else -> {
                return false
            }
        }
    }

    private fun checkBusinessMotor(energyType: EnergyType?): Boolean {
        when (energyType){
            is Petrol95 -> return true
            is Petrol98 -> return true
            is Electricity -> return true
            else -> {
                return false
            }
        }
    }
}
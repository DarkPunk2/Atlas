package com.project.atlas.Services;

import com.project.atlas.Exceptions.VehicleWrongBusinessRulesException;
import com.project.atlas.Interfaces.VehicleInterface;
import com.project.atlas.Models.VehicleModel;


class VehicleService(private val dbService: VehicleDatabaseService) : VehicleInterface {

    override fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        return dbService.addVehicle(user, vehicle)
    }

    override fun listVehicle(user: String): List<VehicleModel>? {
        return dbService.listVehicle(user)
    }

    @Throws(VehicleWrongBusinessRulesException::class)
    fun checkBusinessRules(vehicle: VehicleModel): Boolean {
        throw VehicleWrongBusinessRulesException("Vehículo no válido")
    }
}
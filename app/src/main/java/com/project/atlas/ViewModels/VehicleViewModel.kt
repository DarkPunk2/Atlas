package com.project.atlas.ViewModels

import com.project.atlas.Interfaces.VehicleInterface
import com.project.atlas.Models.VehicleModel

class VehicleViewModel:VehicleInterface {

    override fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        return false;
    }

    override fun listVehicle(user: String): List<VehicleModel>? {
        return emptyList();
    }
}
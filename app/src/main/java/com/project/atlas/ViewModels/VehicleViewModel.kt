package com.project.atlas.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.atlas.Interfaces.VehicleInterface
import com.project.atlas.Models.VehicleModel

open class VehicleViewModel(): ViewModel() {

    suspend fun addVehicle(user: String, vehicle: VehicleModel): Boolean {
        return false;
    }

    suspend fun listVehicle(user: String): List<VehicleModel>? {
        return emptyList()
    }
    open fun listViewVehicles() : LiveData<List<VehicleModel>> {
        return listViewVehicles()
    }

}
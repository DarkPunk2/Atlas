package com.project.atlas.Interfaces

import com.project.atlas.Models.VehicleModel

interface VehicleInterface {

    fun addVehicle(user: String, vehicle: VehicleModel):Boolean
    fun checkEntry(user: String, vehicle: VehicleModel):Boolean
}
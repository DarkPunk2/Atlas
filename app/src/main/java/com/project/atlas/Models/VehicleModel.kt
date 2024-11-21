package com.project.atlas.Models
import com.project.atlas.Interfaces.EnergyType

data class VehicleModel(
    val alias:String?,
    val type: String?,
    val energyType: EnergyType?,
    val consumption:Double?
)
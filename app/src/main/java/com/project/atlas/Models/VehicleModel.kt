package com.project.atlas.Models
import com.project.atlas.interfaces.EnergyType

DataClass VehicleModel (
    alias:String,
    type:Double,
    energyType: EnergyType,
    consumption:Double
)
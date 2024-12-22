package com.project.atlas.interfaces

class Petrol95 : EnergyType() {
    override val typeName = "Petrol 95"
    override val magnitude = "L/100 KM"
    override fun calculateCost(distance: Double, consumption: Double, fuelPrice: Double) {
        TODO("Not yet implemented")
    }
}
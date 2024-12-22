package com.project.atlas.interfaces

abstract class EnergyType {
    abstract val typeName: String
    abstract val magnitude: String
    override fun toString(): String {
        return typeName
    }
    abstract fun calculateCost(distance: Double, consumption: Double, fuelPrice: Double)
}
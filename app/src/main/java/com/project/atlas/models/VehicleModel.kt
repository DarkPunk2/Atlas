package com.project.atlas.models
import com.project.atlas.interfaces.EnergyType


enum class VehicleType {
    Coche, Moto, Patinete, Andar, Bicicleta
}

data class VehicleModel(
    var alias:String?,
    var type: VehicleType,
    var energyType: EnergyType?,
    var consumption:Double?
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VehicleModel) return false
        return alias == other.alias &&
                type == other.type &&
                energyType == other.energyType &&
                consumption == other.consumption
    }

    override fun hashCode(): Int {
        return listOf(alias, type, energyType, consumption).hashCode()
    }
}


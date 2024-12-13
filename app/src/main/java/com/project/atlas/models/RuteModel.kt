package com.project.atlas.models

import java.util.UUID

data class RuteModel(val id: String = UUID.randomUUID().toString(),
                     val start: Location,
                     val end: Location,
                     val vehicle: VehicleModel,
                     val ruteType: RuteType,
                     val distance: Double,
                     val duration: Double,
                     val rute: String,
                     val bbox: List<Double>
)

enum class RuteType {
    FASTER,
    RECOMMENDED,
    SHORTER;
    fun getPreference(): String {
        return when (this) {
            FASTER -> "fastest"
            RECOMMENDED -> "recommended"
            SHORTER -> "shortest"
        }
    }
}
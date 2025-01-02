package com.project.atlas.models

import java.util.UUID

data class RouteModel(val id: String = UUID.randomUUID().toString(),
                      val start: Location,
                      val end: Location,
                      val vehicle: VehicleModel,
                      val routeType: RouteType,
                      val distance: Double,
                      val duration: Double,
                      val rute: String,
                      val bbox: List<Double>,
                      var price: Double? = null
)
enum class RouteType {
    FASTER,
    CHEAPER,
    SHORTER;
    fun getPreference(): String {
        return when (this) {
            FASTER -> "fastest"
            CHEAPER -> "cheaper"
            SHORTER -> "shortest"
        }
    }
}
package com.project.atlas.models

import com.project.atlas.apisRequest.RuteType

data class RuteModel(val start: Location,
                     val end: Location,
                     val vehicle: VehicleModel,
                     val ruteType: RuteType,
                     val distance: Double,
                     val duration: Double,
                     val rute: String
)
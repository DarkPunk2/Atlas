package com.project.atlas.models

import com.google.gson.JsonObject

data class RuteModel(var start: Location,var end: Location,var vehicle: VehicleModel){
    fun getDistance(): Double{
        return -1.0
    }
    fun getDuration(): Int{
        return -1
    }
}

enum class RuteType{
    FASTER,
    CHEAPER,
    SHORTER
}
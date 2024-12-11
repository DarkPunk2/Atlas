package com.project.atlas.services

import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.interfaces.Database
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.VehicleModel

class RuteService(db: Database) {
    fun createRute(start: Location, end: Location, vehicle: VehicleModel, ruteType: RuteType): RuteModel{
        return RuteModel(start,end,vehicle)
    }

    fun addRute(rute: RuteModel): Boolean{
        return false
    }

    fun getRutes(): List<RuteModel>{
        return listOf()
    }

}
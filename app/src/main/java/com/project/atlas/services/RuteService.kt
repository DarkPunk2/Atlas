package com.project.atlas.services

import com.project.atlas.interfaces.Database
import com.project.atlas.models.Location
import com.project.atlas.apisRequest.ResponseDataForRute
import com.project.atlas.apisRequest.RuteType
import com.project.atlas.models.RuteModel
import com.project.atlas.models.VehicleModel


class RuteService(db: Database) {
    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, ruteType: RuteType): RuteModel {
        val coordinates = listOf(listOf(start.lon,start.lat), listOf(end.lon,end.lat))
        val response = ApiClient.fetchRute(coordinates,ruteType.getPreference(), vehicle.type!!)
        return RuteModel(start,end,vehicle,ruteType,response.getDistance(),response.getDuration(),response.getRute())
    }

    fun addRute(rute: RuteModel): Boolean{
        return false
    }

    fun getRutes(): List<RuteModel>{
        return listOf()
    }

}
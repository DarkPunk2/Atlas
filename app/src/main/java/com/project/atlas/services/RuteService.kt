package com.project.atlas.services

import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.interfaces.RuteDatabase
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel


class RuteService(private val db: RuteDatabase) {
    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, ruteType: RuteType): RuteModel {
        val coordinates = listOf(listOf(start.lon,start.lat), listOf(end.lon,end.lat))
        val response = ApiClient.fetchRute(coordinates,ruteType.getPreference(), vehicle.type.toRoute())
        return RuteModel(
            start = start,
            end = end,
            vehicle = vehicle,
            ruteType = ruteType,
            distance = response.getDistance(),
            duration = response.getDuration(),
            rute = response.getRute()
        )
    }

    suspend fun addRute(rute: RuteModel): Boolean{
        return db.add(rute)
    }

    suspend fun getRutes(): List<RuteModel>{
        if (UserModel.getAuthState() == AuthState.Unauthenticated){
            throw UserNotLoginException("User is not login")
        }
        return db.getAll()
    }

}
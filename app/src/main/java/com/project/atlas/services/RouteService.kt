package com.project.atlas.services

import com.project.atlas.exceptions.RouteAlreadyInDataBaseException
import com.project.atlas.exceptions.RouteNotFoundException
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel


class RouteService(private val db: RouteDatabase) {
    var routeApi = ApiClient

    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val coordinates = listOf(listOf(start.lon,start.lat), listOf(end.lon,end.lat))
        val response = routeApi.fetchRoute(coordinates,routeType.getPreference(), vehicle.type.toRoute())
        val route =  RouteModel(
            start = start,
            end = end,
            vehicle = vehicle,
            routeType = routeType,
            distance = response.getDistance(),
            duration = response.getDuration(),
            rute = response.getRute(),
            bbox = response.bbox
        )
        return route
    }

    suspend fun addRoute(route: RouteModel): Boolean{
        if (db.checkForDuplicates(UserModel.eMail, route.id)) {
            throw RouteAlreadyInDataBaseException("Route already exists in the database")
        }
        return db.add(route)
    }

    suspend fun getRoutes(): List<RouteModel>{
        if (UserModel.getAuthState() == AuthState.Unauthenticated){
            throw UserNotLoginException("User is not login")
        }
        return db.getAll()
    }

    suspend fun removeRoute(routeID: String): Boolean{
        if (!db.checkForDuplicates(UserModel.eMail, routeID)) {
            throw RouteNotFoundException("Route $routeID does not exist in the database")
        }
        return db.remove(routeID)
    }

}
package com.project.atlas.services

import com.project.atlas.exceptions.RouteAlreadyInDataBaseException
import com.project.atlas.exceptions.RouteNotFoundException
import com.project.atlas.exceptions.RouteTypeAlreadyAssignedException
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.repository.FuelPriceRepository


class RouteService(private val db: RouteDatabase) {
    var routeApi = ApiClient
    var consumtionService = FuelPriceService(FuelPriceRepository())

    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val coordinates = listOf(listOf(start.lon,start.lat), listOf(end.lon,end.lat))
        if (routeType.getPreference() == "cheaper"){
            val shorter = createRute(start,end,vehicle,RouteType.SHORTER)
            val faster = createRute(start,end,vehicle,RouteType.FASTER)
            val shortCost = consumtionService.calculateRoutePrice(shorter)
            val fastCost = consumtionService.calculateRoutePrice(faster)
            if (shortCost != null && fastCost != null) {
                if (shortCost < fastCost){
                    shorter.routeType = RouteType.CHEAPER
                    return shorter
                }
                faster.routeType = RouteType.CHEAPER
                return faster
            }
            throw ServiceNotAvailableException("Route cost can not be calculated")
        }else {
            val response =
                routeApi.fetchRoute(coordinates, routeType.getPreference(), vehicle.type.toRoute())
            return RouteModel(
                start = start,
                end = end,
                vehicle = vehicle,
                routeType = routeType,
                distance = response.getDistance(),
                duration = response.getDuration(),
                rute = response.getRute(),
                bbox = response.bbox
            )
        }
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

    suspend fun addDefaultRouteType(routeType: RouteType): Boolean{
        try {
            if (getDefaultRouteType() == routeType){
                throw RouteTypeAlreadyAssignedException("This RouteType is already assigned")
            }
        }catch (e: NoSuchElementException){}
        return db.addDefaultRouteType(routeType)
    }

    suspend fun getDefaultRouteType(): RouteType{
        return db.getDefaultRouteType()
    }
}
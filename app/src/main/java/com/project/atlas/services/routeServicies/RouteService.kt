package com.project.atlas.services.routeServicies

import androidx.compose.ui.Modifier
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.exceptions.RouteAlreadyInDataBaseException
import com.project.atlas.exceptions.RouteNotFoundException
import com.project.atlas.exceptions.RouteTypeAlreadyAssignedException
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.interfaces.CreateRouteStrategy
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.ApiClient
import com.project.atlas.services.FuelPriceService


class RouteService(private val db: RouteDatabase) {
    var routeApi = ApiClient
    var consumtionService = FuelPriceService(FuelPriceRepository())

    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        if (start.lon == end.lon && start.lat == end.lat){
            throw InvalidRouteException("The start and end of a route cannot be the same")
        }

        val createRouteStrategy: CreateRouteStrategy = when (routeType) {
            RouteType.SHORTER -> ShorterRouteStrategy(routeApi)
            RouteType.FASTER -> FasterRouteStrategy(routeApi)
            RouteType.CHEAPER -> CheaperRouteStrategy(
                ShorterRouteStrategy(routeApi),
                FasterRouteStrategy(routeApi),
                consumtionService
            )
        }

        return createRouteStrategy.createRoute(start, end, vehicle, routeType)
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
        }catch (_: NoSuchElementException){}
        return db.addDefaultRouteType(routeType)
    }

    suspend fun getDefaultRouteType(): RouteType{
        return db.getDefaultRouteType()
    }
}
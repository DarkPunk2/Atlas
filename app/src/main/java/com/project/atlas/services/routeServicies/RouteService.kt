package com.project.atlas.services.routeServicies

import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.exceptions.RouteAlreadyInDataBaseException
import com.project.atlas.exceptions.RouteNotFoundException
import com.project.atlas.exceptions.RouteTypeAlreadyAssignedException
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.facades.EnergyCostCalculatorFacade
import com.project.atlas.interfaces.CalculateRoute
import com.project.atlas.interfaces.CreateRouteStrategy
import com.project.atlas.interfaces.EnergyCostCalculatorInterface
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel



class RouteService(private val db: RouteDatabase) {
    var routeApi: CalculateRoute = CalculateRouteAdapter()
    var costCalculator: EnergyCostCalculatorInterface = EnergyCostCalculatorFacade()

    suspend fun createRute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        if (!isValidCoordinate(start.lon, start.lat)) {
            throw InvalidRouteException("Invalid coordinates for start location")
        }

        if (!isValidCoordinate(end.lon, end.lat)) {
            throw InvalidRouteException("Invalid coordinates for end location")
        }

        if (start.lon == end.lon && start.lat == end.lat){
            throw InvalidRouteException("The start and end of a route cannot be the same")
        }

        val createRouteStrategy: CreateRouteStrategy = when (routeType) {
            RouteType.SHORTER -> ShorterRouteStrategy(routeApi)
            RouteType.FASTER -> FasterRouteStrategy(routeApi)
            RouteType.CHEAPER -> CheaperRouteStrategy(
                ShorterRouteStrategy(routeApi),
                FasterRouteStrategy(routeApi),
                costCalculator
            )
        }

        return createRouteStrategy.createRoute(start, end, vehicle, routeType)
    }

    private fun isValidCoordinate(lon: Double, lat: Double): Boolean {
        return lon in -180.0..180.0 && lat in -90.0..90.0
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
        val routeList = db.getAll()
        val sortedRouteList = routeList.sortedByDescending { it.isFavorite }
        return sortedRouteList
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

    suspend fun updateRoute(route: RouteModel): Boolean {
        return db.update(route)  // Método para actualizar la ruta en la base de datos
    }
}
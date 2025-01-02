package com.project.atlas.services.routeServicies

import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.CalculateRoute
import com.project.atlas.interfaces.CreateRouteStrategy
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.FuelPriceService


class ShorterRouteStrategy(private val routeApi: CalculateRoute): CreateRouteStrategy {
    override suspend fun createRoute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val response = routeApi.fetchRoute(start,end,vehicle,RouteType.SHORTER)
        return RouteModel(
            start = start,
            end = end,
            vehicle = vehicle,
            routeType = routeType,
            distance = response.distance,
            duration = response.duration,
            rute = response.route,
            bbox = response.bbox
        )
    }
}

class FasterRouteStrategy(private val routeApi: CalculateRoute): CreateRouteStrategy {
    override suspend fun createRoute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val response = routeApi.fetchRoute(start,end,vehicle,RouteType.FASTER)
        return RouteModel(
            start = start,
            end = end,
            vehicle = vehicle,
            routeType = routeType,
            distance = response.distance,
            duration = response.duration,
            rute = response.route,
            bbox = response.bbox
        )
    }
}

class CheaperRouteStrategy(
    private val shorterRouteStrategy: CreateRouteStrategy,
    private val fasterRouteStrategy: CreateRouteStrategy,
    private val consumtionService: FuelPriceService
): CreateRouteStrategy {
    override suspend fun createRoute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val shorter = shorterRouteStrategy.createRoute(start, end, vehicle,routeType)
        val faster = fasterRouteStrategy.createRoute(start, end, vehicle,routeType)
        val shortCost = consumtionService.calculateRoutePrice(shorter)
        val fastCost = consumtionService.calculateRoutePrice(faster)

        return if (shortCost != null && fastCost != null) {
            if (shortCost < fastCost) {
                shorter
            } else {
                faster
            }
        } else {
            throw ServiceNotAvailableException("Route cost cannot be calculated")
        }
    }
}


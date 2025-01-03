package com.project.atlas.services.routeServicies

import com.project.atlas.interfaces.CalculateRoute
import com.project.atlas.interfaces.CreateRouteStrategy
import com.project.atlas.interfaces.EnergyCostCalculatorInterface
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel


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
    private val costCalculator: EnergyCostCalculatorInterface
): CreateRouteStrategy {
    override suspend fun createRoute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel {
        val shorter = shorterRouteStrategy.createRoute(start, end, vehicle,routeType)
        val faster = fasterRouteStrategy.createRoute(start, end, vehicle,routeType)
        val shortCost = costCalculator.calculateCost(shorter)
        val fastCost = costCalculator.calculateCost(faster)

        return if (shortCost < fastCost) {
            shorter
        } else {
            faster
        }
    }
}


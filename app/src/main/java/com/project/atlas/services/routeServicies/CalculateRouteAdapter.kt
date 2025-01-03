package com.project.atlas.services.routeServicies

import com.project.atlas.apisRequest.RouteData
import com.project.atlas.interfaces.CalculateRoute
import com.project.atlas.models.Location
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.OpenRouteServiceAPI

class CalculateRouteAdapter: CalculateRoute {
    override suspend fun fetchRoute(
        start: Location,
        end: Location,
        vehicleModel: VehicleModel,
        routeType: RouteType
    ): RouteData {
        val coordinates = listOf(listOf(start.lon, start.lat), listOf(end.lon, end.lat))
        val response = OpenRouteServiceAPI.fetchRoute(coordinates, routeType.getPreference(), vehicleModel.type.toRoute())
        return RouteData(
            duration = response.getDuration(),
            distance = response.getDistance(),
            route = response.getRoute(),
            bbox = response.bbox
        )
    }
}
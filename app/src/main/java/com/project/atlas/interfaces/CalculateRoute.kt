package com.project.atlas.interfaces

import com.project.atlas.apisRequest.RouteData
import com.project.atlas.models.Location
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel

interface CalculateRoute {
    suspend fun fetchRoute(start: Location, end: Location, vehicleModel: VehicleModel, routeType: RouteType ): RouteData
}
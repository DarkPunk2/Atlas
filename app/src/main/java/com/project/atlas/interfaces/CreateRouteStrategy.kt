package com.project.atlas.interfaces

import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel

interface CreateRouteStrategy {
    suspend fun createRoute(start: Location, end: Location, vehicle: VehicleModel, routeType: RouteType): RouteModel
}
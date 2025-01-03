package com.project.atlas.interfaces

import com.google.firebase.firestore.DocumentReference
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel

interface RouteDatabase {
    suspend fun add(route: RouteModel): Boolean
    suspend fun getAll(): List<RouteModel>
    suspend fun remove(routeID: String): Boolean
    suspend fun checkForDuplicates(user: String, id: String): Boolean
    suspend fun addDefaultRouteType(routeType: RouteType): Boolean
    suspend fun getDefaultRouteType(): RouteType
    suspend fun update(route: RouteModel): Boolean
    suspend fun deleteInvalidRoutes()
}
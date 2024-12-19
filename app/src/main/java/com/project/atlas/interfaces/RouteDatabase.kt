package com.project.atlas.interfaces

import com.project.atlas.models.RouteModel

interface RouteDatabase {
    suspend fun add(route: RouteModel): Boolean
    suspend fun getAll(): List<RouteModel>
    suspend fun remove(routeID: String): Boolean
}
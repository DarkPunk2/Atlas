package com.project.atlas.services

import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType

class FailDataBaseService: RouteDatabase {
    override suspend fun add(route: RouteModel): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun getAll(): List<RouteModel> {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun remove(routeID: String): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun checkForDuplicates(user: String, id: String): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun addDefaultRouteType(routeType: RouteType): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun getDefaultRouteType(): RouteType {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun update(route: RouteModel): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun deleteInvalidRoutes() {
        throw ServiceNotAvailableException("Service is not available")
    }
}
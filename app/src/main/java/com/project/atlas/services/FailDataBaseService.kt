package com.project.atlas.services

import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.RouteDatabase
import com.project.atlas.models.RouteModel

class FailDataBaseService: RouteDatabase {
    override suspend fun add(rute: RouteModel): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun getAll(): List<RouteModel> {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun remove(routeID: String): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }
}
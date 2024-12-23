package com.project.atlas.services

import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.RuteDatabase
import com.project.atlas.models.RuteModel

class FailDataBaseService: RuteDatabase {
    override suspend fun add(rute: RuteModel): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override suspend fun getAll(): List<RuteModel> {
        throw ServiceNotAvailableException("Service is not available")
    }

    override fun remove(): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }
}
package com.project.atlas.services

import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.interfaces.Database

class FailDataBaseService: Database {
    override fun add(): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }

    override fun getAll() {
        throw ServiceNotAvailableException("Service is not available")
    }

    override fun remove(): Boolean {
        throw ServiceNotAvailableException("Service is not available")
    }
}
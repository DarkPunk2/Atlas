package com.project.atlas.interfaces

import com.project.atlas.models.Location

interface LocationRepositoryInterface {
    fun addLocation(location: Location)
    suspend fun getAllLocations(): List<Location>
    fun deleteLocation(location: Location)
    fun updateLocation(location: Location, lat: Double, lon: Double, alias: String, toponym: String, favourite: Boolean)
    fun setFavourite(location: Location, value: Boolean)
    }
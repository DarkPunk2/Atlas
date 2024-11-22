package com.project.atlas.ViewModels

import com.project.atlas.Models.Location

class LocationViewModel {
    private val locationsList = listOf<Location>()

    fun addLocation(location: Location ) {

    }

    fun getNumLocations(): Int {
        return locationsList.size
    }

    fun getLocation(index: Int): Location {
        return locationsList.get(0)
    }

    fun getAllLocations(): List<Location> {
        return locationsList
    }
}
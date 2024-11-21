package com.project.atlas.Services

import com.project.atlas.Models.Location

class LocationRepository {
    private val locationsList = listOf<Location>();
    fun getNumLocations(): Int {
        return locationsList.size;
    }

    fun getLocation(i: Int): Location {
        return locationsList.get(0);
    }
}
package com.project.atlas.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.Location
import com.project.atlas.services.ApiClient
import com.project.atlas.services.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class LocationsViewModel: ViewModel() {
    private val locationsList = SnapshotStateList<Location>()
    private val locationRepository = LocationRepository()

    fun addLocation(location: Location ) {
        locationsList.add(location)
    }

    fun addLocation(lat: Double, lon: Double, alias: String) {
        if(abs(lat)<=90.0 && abs(lon)<=180.0) {
            viewModelScope.launch {
                val newAlias = if (alias.isEmpty()) {
                    ApiClient.fetchToponymByLatLong(
                        "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                        lat.toString(),
                        lon.toString()
                    )
                } else {
                    alias
                }

                //Sanitize alias to remove any / characters and everything until a comma
                val sanitizedAlias = newAlias.replace(Regex("/[^,]*,"), ",")
                val newLocation = Location(lat, lon, sanitizedAlias)
                locationRepository.addLocation(newLocation)
                addLocation(newLocation)
            }
        }
    }

    fun getNumLocations(): Int {
        return locationsList.size
    }

    fun getLocation(index: Int): Location {
        if (index<getNumLocations()) {
            return locationsList.get(index)
        }
        throw IndexOutOfBoundsException()
    }

    fun getAllLocations(): List<Location> {
        viewModelScope.launch(Dispatchers.IO) {
            val dbLocationsList = locationRepository.getAllLocations()
            locationsList.clear()
            locationsList.addAll(dbLocationsList)
            Log.d(TAG, "Locations size viewModel: ${dbLocationsList.size}")
        }
        return locationsList
    }

    fun removeLocation(location: Location) {
        if (locationsList.size > 0) {
            locationRepository.deleteLocation(location)
            locationsList.remove(location)
        } else {
            throw IllegalStateException()
        }
    }

    fun updateAlias(location: Location, newAlias: String) {

    }
}
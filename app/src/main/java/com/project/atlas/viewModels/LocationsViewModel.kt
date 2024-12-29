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

class LocationsViewModel : ViewModel() {
    private val locationsList = SnapshotStateList<Location>()
    private val locationRepository = LocationRepository()

    fun addLocation(location: Location) {
        if (abs(location.lat) > 90.0 || abs(location.lon) > 180.0) {
            throw IllegalArgumentException()
        }
        locationsList.add(location)
    }

    fun addLocation(lat: Double, lon: Double, alias: String) {
        //Realizar corutina
        viewModelScope.launch {
            //Conseguir latitud y longitud de la API
            val toponym =
                ApiClient.fetchToponymByLatLong(
                    "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                    lat.toString(),
                    lon.toString()
                )

            //Sanitizar topónimo para eliminar cualquier carácter después de una '/' hasta una ',' .
            val sanitizedToponym = toponym.replace(Regex("/[^,]*,"), ",")
            val newLocation = Location(lat, lon, alias, sanitizedToponym)
            locationRepository.addLocation(newLocation)
            addLocation(newLocation)
        }
    }

    fun addLocation(toponym: String) {
        if (toponym.isNotEmpty()) {
            ApiClient.fetchGeocode(
                "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                toponym
            ) { lat, lon, topo ->
                addLocation(lat, lon, topo)
                Log.d("location", "Nueva ubicación agregada: $topo")
            }
        } else {
            Log.e("location", "No se encontraron resultados en la geocodificación.")
        }
    }

    fun getNumLocations(): Int {
        return locationsList.size
    }

    fun getLocation(index: Int): Location {
        if (index < getNumLocations()) {
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

    fun updateLocation(location: Location, lat: Double, lon: Double) {
        this.updateLocation(location, lat, lon, location.alias)
    }

    fun updateLocation(location: Location, alias: String) {
        this.updateLocation(location, location.lat, location.lon, alias)
    }

    fun updateLocation(location: Location, lat: Double, lon: Double, alias: String) {
        var newAlias = location.alias
        if (abs(lat) > 90.0 || abs(lon) > 180.0) {
            throw IllegalArgumentException()
        }
        if (alias != location.alias) {
            newAlias = alias
        }
        locationRepository.updateLocation(location, lat, lon, newAlias)
        location.lat = lat
        location.lon = lon
        location.alias = newAlias
    }

    fun changeFavourite(location: Location) {
        if(locationsList.contains(location)){
            location.changeFavourite()
            locationsList.sortWith(compareByDescending { it.isFavourite })
        }
        else throw IllegalArgumentException()
    }
}
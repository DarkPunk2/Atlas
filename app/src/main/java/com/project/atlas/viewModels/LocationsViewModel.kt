package com.project.atlas.viewModels

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.interfaces.LocationRepositoryInterface
import com.project.atlas.models.Location
import com.project.atlas.services.OpenRouteServiceAPI
import com.project.atlas.repositories.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class LocationsViewModel(
    private val locationRepository: LocationRepositoryInterface = LocationRepository()
) : ViewModel() {
    private val locationsList = SnapshotStateList<Location>()
    var locationsApi = OpenRouteServiceAPI

    fun addLocation(location: Location) {
        if (abs(location.lat) > 90.0 || abs(location.lon) > 180.0) {
            throw IllegalArgumentException()
        }
        locationsList.add(location)
    }

    fun addLocation(lat: Double, lon: Double, alias: String) {
        //Comprobar latitud y longitud
        if (abs(lat) > 90.0 || abs(lon) > 180.0) {
            throw IllegalArgumentException()
        }

        //Realizar corutina
        viewModelScope.launch(Dispatchers.IO) {
            var newAlias = alias
            //Conseguir topónimo de la API
            val toponym =
                locationsApi.fetchToponymByLatLong(
                    "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                    lat.toString(),
                    lon.toString()
                )

            //Sanitizar topónimo para eliminar cualquier carácter después de una '/' hasta una ',' .
            val sanitizedToponym = toponym.replace(Regex("/[^,]*,"), ",")

            if (alias.isBlank()) {
                newAlias = toponym
            }

            val newLocation = Location(lat, lon, newAlias, sanitizedToponym)
            locationRepository.addLocation(newLocation)
            addLocation(newLocation)
        }
    }

    fun addLocation(toponym: String) {
        if (toponym.isNotEmpty()) {
            locationsApi.fetchGeocode(
                "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                toponym
            ) { lat, lon, topo ->
                addLocation(lat, lon, topo)
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
            locationsList.sortWith(compareByDescending { it.isFavourite })
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
        if (alias.isBlank()) {
            newAlias = location.toponym
        }
        locationRepository.updateLocation(
            location,
            lat,
            lon,
            newAlias,
            location.toponym,
            location.isFavourite
        )
        location.lat = lat
        location.lon = lon
        location.alias = newAlias
    }

    fun changeFavourite(location: Location) {
        if (locationsList.contains(location)) {
            location.changeFavourite()
            locationsList.sortWith(compareByDescending { it.isFavourite })

            locationRepository.setFavourite(location, location.isFavourite)
        } else throw IllegalArgumentException()
    }

    suspend fun getToponym(lat: Double, lon: Double): String {
        return withContext(Dispatchers.IO) {
            // Conseguir topónimo de la API
            locationsApi.fetchToponymByLatLong(
                "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                lat.toString(),
                lon.toString()
            )
        }
    }

}
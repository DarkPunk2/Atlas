package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.Location
import com.project.atlas.services.MapService
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel(private val mapService: MapService) : ViewModel() {

    private val _markerPosition = MutableLiveData(GeoPoint(39.992573, -0.064749))
    val markerPosition: LiveData<GeoPoint> = _markerPosition

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation

    init {
        viewModelScope.launch {
            try {
                val userLocation = mapService.getUserLocation()
                if (userLocation != null) {
                    _markerPosition.value = GeoPoint(userLocation.lat, userLocation.lon)
                } else {
                    _markerPosition.value = GeoPoint(39.992573, -0.064749)
                }
            } catch (e: Exception) {
                _markerPosition.value = GeoPoint(39.992573, -0.064749)
            }
        }
    }

    fun setMarkerPosition(newPosition: GeoPoint) {
        _markerPosition.value = newPosition
    }

    private val _showMarker = MutableLiveData(false)
    val showMarker: LiveData<Boolean> = _showMarker

    fun setShowMarker(visible: Boolean) {
        _showMarker.value = visible
    }

    fun hasPermission(): Boolean {
        return mapService.hasLocationPermissions()
    }

    fun getUserLocation() {
        viewModelScope.launch {
            try {
                _userLocation.value = mapService.getUserLocation()
            } catch (e: Exception) {
                _userLocation.value = null
            }
        }
    }

    fun goToUser(){
        viewModelScope.launch {
            try {
                val user = mapService.getUserLocation()
                if (user != null) {
                    _markerPosition.value = GeoPoint(user.lat, user.lon)
                }
            } catch (_: Exception) { }
        }
    }

    fun resetUserLocation(){
        _userLocation.value = null
    }
}


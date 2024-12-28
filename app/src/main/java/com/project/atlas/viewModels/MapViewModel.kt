package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.services.MapService
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel(private val mapService: MapService) : ViewModel() {

    private val _markerPosition = MutableLiveData(GeoPoint(39.992573, -0.064749))
    val markerPosition: LiveData<GeoPoint> = _markerPosition

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
}


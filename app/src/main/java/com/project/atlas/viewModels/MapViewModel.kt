package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.atlas.models.MapState
import org.osmdroid.util.GeoPoint

class MapViewModel: ViewModel() {

    private val _markerPosition = MutableLiveData(GeoPoint(39.992573, -0.064749))
    val markerPosition: LiveData<GeoPoint> = _markerPosition

    fun setMarkerPosition(newPosition: GeoPoint) {
        _markerPosition.value = newPosition
    }

    private val _showMarker = MutableLiveData(false)
    val showMarker: LiveData<Boolean> = _showMarker

    fun setShowMarker(visible: Boolean) {
        _showMarker.value = visible
    }
}

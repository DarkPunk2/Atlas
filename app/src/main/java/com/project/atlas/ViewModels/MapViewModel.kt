package com.project.atlas.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.atlas.Models.MapState
import org.osmdroid.util.GeoPoint

class MapViewModel: ViewModel() {
    private val _mapState = MutableLiveData<MapState>()
    val mapState: LiveData<MapState> = _mapState

    init {
        _mapState.value = MapState(GeoPoint(39.993100, -0.067035),16.0,GeoPoint(0.0,0.0),GeoPoint(0.0,0.0))
    }

    fun addPoint(point: GeoPoint){
        _mapState.value!!.initialPoint = point
    }
}
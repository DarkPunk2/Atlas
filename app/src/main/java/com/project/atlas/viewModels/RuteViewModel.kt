package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.RuteDatabaseService
import com.project.atlas.services.RuteService
import kotlinx.coroutines.launch

class RuteViewModel: ViewModel() {
    private val _ruteState = MutableLiveData<RuteModel>()
    val ruteState: LiveData<RuteModel> = _ruteState

    private val _vehicle = MutableLiveData<VehicleModel>()
    val vehicleState: LiveData<VehicleModel> = _vehicle

    private val _start = MutableLiveData<Location>()
    val start: LiveData<Location> = _start

    private val _end = MutableLiveData<Location>()
    val end: LiveData<Location> = _end


    private val ruteService = RuteService(RuteDatabaseService())

    fun createRute(start: Location?, end: Location?, vehicle: VehicleModel?, ruteType: RuteType?) {
        if (start != null && end != null && vehicle != null && ruteType != null) {
            viewModelScope.launch {
                _ruteState.value = ruteService.createRute(start, end, vehicle, ruteType)
            }
        }
    }

    fun addVehicle(vehicle: VehicleModel){
        _vehicle.value = vehicle
    }

    fun addStart(start: Location){
        _start.value = start
    }

    fun addEnd(end: Location){
        _end.value = end
    }
}
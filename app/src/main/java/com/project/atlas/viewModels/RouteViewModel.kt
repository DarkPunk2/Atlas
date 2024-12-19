package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import kotlinx.coroutines.launch

class RouteViewModel: ViewModel() {
    private val _routeState = MutableLiveData<RouteModel>()
    val routeState: LiveData<RouteModel> = _routeState

    private val _vehicle = MutableLiveData<VehicleModel>()
    val vehicleState: LiveData<VehicleModel> = _vehicle

    private val _start = MutableLiveData<Location>()
    val start: LiveData<Location> = _start

    private val _end = MutableLiveData<Location>()
    val end: LiveData<Location> = _end

    private val _ruteList = MutableLiveData<List<RouteModel>>()
    val ruteList: LiveData<List<RouteModel>> = _ruteList


    private val routeService = RouteService(RouteDatabaseService())

    fun createRute(start: Location?, end: Location?, vehicle: VehicleModel?, routeType: RouteType?) {
        if (start != null && end != null && vehicle != null && routeType != null) {
            viewModelScope.launch {
                _routeState.value = routeService.createRute(start, end, vehicle, routeType)
            }
        }
    }

    fun addRoute(ruteModel: RouteModel){
        viewModelScope.launch {
            routeService.addRoute(ruteModel)
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

    suspend fun getRutes(){
        _ruteList.postValue(routeService.getRoutes())
    }
}
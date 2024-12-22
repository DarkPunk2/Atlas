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
    private val _navigateToRuteView = MutableLiveData(false)
    val navigateToRuteView: LiveData<Boolean> = _navigateToRuteView

    private val _showAddButton = MutableLiveData(false)
    val showAddButton: LiveData<Boolean> = _showAddButton

    private val _showRemoveButton = MutableLiveData(false)
    val showRemoveButton: LiveData<Boolean> = _showRemoveButton

    private var _routeState = MutableLiveData<RouteModel?>()
    val routeState: LiveData<RouteModel?> = _routeState

    private var _vehicle = MutableLiveData<VehicleModel?>()
    val vehicleState: LiveData<VehicleModel?> = _vehicle

    private var _start = MutableLiveData<Location?>()
    val start: LiveData<Location?> = _start

    private var _end = MutableLiveData<Location?>()
    val end: LiveData<Location?> = _end

    private val _ruteList = MutableLiveData<List<RouteModel>>()
    val ruteList: LiveData<List<RouteModel>> = _ruteList

    private val _showStartSelect = MutableLiveData(false)
    val showStartSelect = _showStartSelect

    private val _showEndSelect = MutableLiveData(false)
    val showEndSelect = _showEndSelect


    private val routeService = RouteService(RouteDatabaseService())

    fun createRute(start: Location?, end: Location?, vehicle: VehicleModel?, routeType: RouteType?) {
        if (start != null && end != null && vehicle != null && routeType != null) {
            viewModelScope.launch {
                _routeState.value = routeService.createRute(start, end, vehicle, routeType)
                _navigateToRuteView.value = true
            }
        }
    }

    fun addRoute(routeModel: RouteModel){
        viewModelScope.launch {
            routeService.addRoute(routeModel)
        }
    }

    fun deleteRoute(){
        viewModelScope.launch {
            if (_routeState.value != null) {
                routeService.removeRoute(_routeState.value!!.id)
            }
        }
    }

    fun addRouteState(routeModel: RouteModel){
        _routeState.value = routeModel
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

    fun seeSelectStart(boolean: Boolean){
        _showStartSelect.value = boolean
    }

    fun seeSelectEnd(boolean: Boolean){
        _showEndSelect.value = boolean
    }

    fun seeAdd(boolean: Boolean){
        _showAddButton.value = boolean
    }

    fun seeRemove(boolean: Boolean){
        _showRemoveButton.value = boolean
    }

    fun resetValues(){
        _vehicle.value = null
        _start.value = null
        _end.value = null
        _navigateToRuteView.value = false
    }
}
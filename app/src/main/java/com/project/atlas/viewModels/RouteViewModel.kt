package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.FuelPriceService
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouteViewModel: ViewModel() {
    private val _navigateToRuteView = MutableLiveData(false)
    val navigateToRuteView: LiveData<Boolean> = _navigateToRuteView

    private val _errorState = MutableLiveData<Exception>()
    val errorState: LiveData<Exception> = _errorState


    private var pricesCalculated = false // Flag para evitar cálculos repetidos
    val fuelPriceService = FuelPriceService(FuelPriceRepository())

    private val _fuelErrorMessage = MutableStateFlow("")
    val fuelErrorMessage: StateFlow<String> get() = _fuelErrorMessage

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
                try {
                    _routeState.value = routeService.createRute(start, end, vehicle, routeType)
                    _navigateToRuteView.value = true
                } catch (e: Exception){
                    _errorState.value = e
                }

            }
        }
    }

    private val calculatedPrices = mutableMapOf<String, Double?>() // Mapa para almacenar precios calculados

    fun calculatePricesForRoutesIfNeeded() {
        viewModelScope.launch {
            val updatedRoutes = _ruteList.value?.map { route ->
                if (calculatedPrices.containsKey(route.id)) {
                    // Si el precio ya fue calculado, usarlo del mapa
                    route.copy(price = calculatedPrices[route.id])
                } else {
                    // Si el precio no está calculado, calcularlo y guardarlo en el mapa
                    try {
                        val price = fuelPriceService.calculateRoutePrice(route)
                        calculatedPrices[route.id] = price // Guardar en el mapa
                        route.copy(price = price)
                    } catch (e: Exception) {
                        calculatedPrices[route.id] = null // Guardar como no disponible
                        route
                    }
                }
            } ?: emptyList()

            _ruteList.postValue(updatedRoutes)
            pricesCalculated = true // Marca los precios como calculados
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
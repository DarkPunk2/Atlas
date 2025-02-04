package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.facades.EnergyCostCalculatorFacade
import com.project.atlas.interfaces.EnergyCostCalculatorInterface
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.OpenRouteServiceAPI
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteViewModel: ViewModel() {
    private val _navigateToRuteView = MutableLiveData(false)
    val navigateToRuteView: LiveData<Boolean> = _navigateToRuteView

    private val _errorState = MutableLiveData<Exception>()
    val errorState: LiveData<Exception> = _errorState

    private var pricesCalculated = false // Flag para evitar cálculos repetidos

    private val energyCostCalculator: EnergyCostCalculatorInterface = EnergyCostCalculatorFacade()
    private val _calculatedPrice = MutableLiveData<Double?>()
    val calculatedPrice: LiveData<Double?> get() = _calculatedPrice

    private val _showAddButton = MutableLiveData(false)
    val showAddButton: LiveData<Boolean> = _showAddButton

    private val _showRemoveButton = MutableLiveData(false)
    val showRemoveButton: LiveData<Boolean> = _showRemoveButton

    private var _routeState = MutableLiveData<RouteModel?>()
    val routeState: LiveData<RouteModel?> = _routeState

    private var _vehicle = MutableLiveData<VehicleModel?>()
    val vehicleState: LiveData<VehicleModel?> = _vehicle

    private var _vehicleDefaut = MutableLiveData<VehicleModel?>()
    val vehicleDefault: LiveData<VehicleModel?> = _vehicleDefaut

    private var _start = MutableLiveData<Location?>()
    val start: LiveData<Location?> = _start

    private var _end = MutableLiveData<Location?>()
    val end: LiveData<Location?> = _end

    private var _routeTypeState = MutableLiveData<RouteType?>()
    val routeTypeState: LiveData<RouteType?> = _routeTypeState

    private val _ruteList = MutableLiveData<List<RouteModel>>()
    val ruteList: LiveData<List<RouteModel>> = _ruteList

    private val _showStartSelect = MutableLiveData(false)
    val showStartSelect = _showStartSelect

    private val _showEndSelect = MutableLiveData(false)
    val showEndSelect = _showEndSelect

    private val _isFavoriteUpdated = MutableLiveData<Boolean>()
    val isFavoriteUpdated: LiveData<Boolean> get() = _isFavoriteUpdated


    private val routeService = RouteService(RouteDatabaseService())
    private val vehicleService = VehicleService(VehicleDatabaseService())


    fun createRute(start: Location?, end: Location?, vehicle: VehicleModel?, routeType: RouteType?) {
        if (start != null && end != null && routeType != null) {
            viewModelScope.launch {
                try {
                    if (vehicle == null) {
                        _routeState.value =
                            _vehicleDefaut.value?.let {
                                routeService.createRute(start, end,
                                    it, routeType)
                            } ?: throw Exception("Vehicle not selected")
                    } else{
                        _routeState.value = routeService.createRute(start, end, vehicle, routeType)
                    }
                    _navigateToRuteView.value = true
                } catch (e: Exception){
                    _errorState.value = e
                }

            }
        }
    }

    fun defaultVehicle(){
        viewModelScope.launch {
            try {
                _vehicleDefaut.value = vehicleService.getDefaultVehicle(UserModel.eMail)
            }catch (_: Exception){
                _vehicleDefaut.value = null
            }
        }
    }

    fun defaultRouteType(){
        viewModelScope.launch {
            try {
                _routeTypeState.value = routeService.getDefaultRouteType()
            }catch (_: Exception){
                _routeTypeState.value = null
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
                        val price = withContext(Dispatchers.IO) {
                            energyCostCalculator.calculateCost(route)
                        }
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

    fun calculatePrice(route: RouteModel) {
        viewModelScope.launch {
            val price = energyCostCalculator.calculateCost(route)
            _calculatedPrice.postValue(price) // Actualiza el valor en LiveData
        }
    }


    fun changeDefaultRouteType(routeType: RouteType){
        viewModelScope.launch {
            try {
                if (routeService.addDefaultRouteType(routeType)){
                    _routeTypeState.value = routeType
                }
            }catch (_: Exception){

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

    fun addStart(start: Location?){
        _start.value = start
    }

    fun addStartByCoord(lat: Double, lon:Double){
        viewModelScope.launch {
            try {
                val toponym = OpenRouteServiceAPI.fetchToponymByLatLong(
                    "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                    lat.toString(),
                    lon.toString()
                )
                _start.value = Location(lon = lon, lat = lat, alias = toponym, toponym = toponym)
            }catch (_: Exception){
                _start.value = null
            }
        }
    }

    fun addEnd(end: Location?){
        _end.value = end
    }

    fun addEndByCoord(lat: Double, lon:Double){
        viewModelScope.launch {
            try {
                val toponym = OpenRouteServiceAPI.fetchToponymByLatLong(
                    "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f",
                    lat.toString(),
                    lon.toString()
                )
                _end.value = Location(lon = lon, lat = lat, alias = toponym, toponym = toponym)
            }catch (_: Exception){
                _end.value = null
            }
        }
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
        defaultVehicle()
        _calculatedPrice.value = null
        _vehicle.value = null
        _start.value = null
        _end.value = null
        _navigateToRuteView.value = false
    }

    fun updateRouteFavorite(route: RouteModel) {
        viewModelScope.launch {
            try {
                route.changeFavourite()
                routeService.updateRoute(route) // Actualiza la ruta en la base de datos
                getRutes()
            } catch (e: Exception) {
                _isFavoriteUpdated.postValue(false)
            }
        }
    }

}
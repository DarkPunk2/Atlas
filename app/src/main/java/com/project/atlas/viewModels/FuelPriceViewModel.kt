package com.project.atlas.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.models.RouteModel
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.FuelPriceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FuelPriceViewModel() : ViewModel() {
    val fuelPriceService = FuelPriceService(FuelPriceRepository())

    private val _routePrice = MutableStateFlow<Double?>(null)
    val routePrice: StateFlow<Double?> get() = _routePrice

    private val _fuelErrorMessage = MutableStateFlow("")
    val fuelRrrorMessage: StateFlow<String> get() = _fuelErrorMessage

    fun calculatePriceForRoute(route: RouteModel) {
        viewModelScope.launch {
            try {
                _routePrice.value = fuelPriceService.calculateRoutePrice(route)
            } catch (e: Exception) {
                _fuelErrorMessage.value = "Error al calcular el precio de la ruta: ${e.message}"
            }
        }
    }
}

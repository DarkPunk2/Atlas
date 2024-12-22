package com.project.atlas.viewModels

import Diesel
import Petrol98
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.models.FuelPriceModel
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.GeocodeApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.Normalizer
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FuelPriceViewModel : ViewModel() {
    private val repository = FuelPriceRepository()
    private val geocodeApiKey = "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f"

    // StateFlow variables
    private val _priceInfo = MutableStateFlow<List<FuelPriceModel>>(emptyList())
    val priceInfo: StateFlow<List<FuelPriceModel>> get() = _priceInfo

    private val _municipioId = MutableStateFlow("")
    val municipioId: StateFlow<String> get() = _municipioId

    private val _nearestPrice = MutableStateFlow<FuelPriceModel?>(null)
    val nearestPrice: StateFlow<FuelPriceModel?> get() = _nearestPrice

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> get() = _errorMessage

    // Método principal para manejar la búsqueda completa
    fun fetchFuelData(lat: Double, long: Double, energyType: EnergyType) {
        var idProduct = 1
        when (energyType) {
            is Petrol98 -> {
                idProduct = 1
            }
            is Petrol95 -> {
                idProduct = 3
            }
            is Diesel -> {
                idProduct = 4
            }
        }

        viewModelScope.launch {
            try {
                // Paso 1: Obtener el ID de la región
                fetchRegionId(lat, long)

                // Asegurarse de que municipioId esté disponible
                while (_municipioId.value.isEmpty()) {
                    delay(100)
                }

                // Paso 2: Obtener los precios del combustible
                fetchFuelPrices(_municipioId.value, idProduct)

                // Paso 3: Obtener el precio más cercano
                fetchNearestFuelPrice(lat, long)
            } catch (e: Exception) {
                _errorMessage.value = "Error al realizar la búsqueda: ${e.message}"
            }
        }
    }

    // Paso 1: Obtener el ID de la región
    private suspend fun fetchRegionId(lat: Double, long: Double) {
        try {
            val provinceName = suspendCancellableCoroutine<String> { continuation ->
                GeocodeApiService.fetchRegion(geocodeApiKey, lat.toString(), long.toString()) { result ->
                    if (result.isNotEmpty()) {
                        continuation.resume(result)
                    } else {
                        continuation.resumeWithException(Exception("No se encontró la región."))
                    }
                }
            }

            val provincias = repository.getRegions()
            val provincia = provincias.find { it.Provincia.normalize().contains(provinceName.normalize(), ignoreCase = true) }
            _municipioId.value = provincia?.IDPovincia ?: ""
        } catch (e: Exception) {
            _errorMessage.value = "Error al obtener el ID de la región: ${e.message}"
        }
    }

    // Paso 2: Obtener precios del combustible
    private suspend fun fetchFuelPrices(idRegion: String, idProducto: Int) {
        try {
            val response = repository.getFuelPrices(idRegion, idProducto)
            _priceInfo.value = response
        } catch (e: Exception) {
            _errorMessage.value = "Error al obtener los precios: ${e.message}"
        }
    }

    // Paso 3: Obtener la estación más cercana
    private fun fetchNearestFuelPrice(latitude: Double, longitude: Double) {
        val nearest = _priceInfo.value.minByOrNull { station ->
            val lat = station.Latitud.replace(",", ".").toDoubleOrNull() ?: 0.0
            val lon = station.Longitud.replace(",", ".").toDoubleOrNull() ?: 0.0
            calculateDistance(latitude, longitude, lat, lon)
        }
        _nearestPrice.value = nearest
    }

    // Función para normalizar cadenas
    private fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "") // Elimina marcas diacríticas
    }

    // Cálculo de distancia entre coordenadas (Haversine)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
    /*
    private fun calculateRoutePrice(route: RouteModel) {
        val energyType: EnergyType? = route.vehicle.energyType
        val distance = route.distance
        val consumption = route.vehicle.consumption
        val fuelPrice = fetchFuelData(route.start.lat, route.start.lon, energyType)

        if (consumption != null) {
            energyType?.calculateCost(distance, consumption, fuelPrice)
        }

    }*/
}

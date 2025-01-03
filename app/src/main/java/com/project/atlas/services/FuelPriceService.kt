package com.project.atlas.services

import Diesel
import Petrol98
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.models.FuelPriceModel
import com.project.atlas.models.RouteModel
import com.project.atlas.repository.FuelPriceRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.Normalizer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object FuelPriceService {
    private val geocodeApiKey = "5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f"
    private val repository = FuelPriceRepository()
    suspend fun calculateRoutePrice(route: RouteModel): Double? {
        val energyType = route.vehicle.energyType
        val distance = route.distance
        val consumption = route.vehicle.consumption
        val fuelPrice = fetchFuelData(route.start.lat, route.start.lon, energyType!!)

        return if (consumption != null && fuelPrice != null) {
            energyType.calculateCost(distance / 1000, consumption, fuelPrice)
        } else {
            null
        }
    }

    suspend fun fetchFuelData(lat: Double, lon: Double, energyType: EnergyType): Double? {
        val idProduct = when (energyType) {
            is Petrol98 -> 1
            is Petrol95 -> 3
            is Diesel -> 4
            else -> return 0.0// Default
        }

        return try {
            val regionId = fetchRegionId(lat, lon)
            val prices = fetchFuelPrices(regionId, idProduct)
            getNearestFuelPrice(prices, lat, lon)
        } catch (e: Exception) {
            null // Manejo de errores aquí si es necesario
        }
    }

    private suspend fun fetchRegionId(lat: Double, lon: Double): String {
        val provinceName = suspendCancellableCoroutine<String> { continuation ->
            GeocodeApiService.fetchRegion(geocodeApiKey, lat.toString(), lon.toString()) { result ->
                if (result.isNotEmpty()) {
                    continuation.resume(result)
                } else {
                    continuation.resumeWithException(Exception("No se encontró la región."))
                }
            }
        }

        val provinces = repository.getRegions()
        return provinces.find { it.Provincia.normalize().contains(provinceName.normalize(), true) }?.IDPovincia ?: ""
    }

    private suspend fun fetchFuelPrices(regionId: String, productId: Int): List<FuelPriceModel> {
        return repository.getFuelPrices(regionId, productId)
    }

    private fun getNearestFuelPrice(
        prices: List<FuelPriceModel>,
        latitude: Double,
        longitude: Double
    ): Double? {
        return prices.minByOrNull { station ->
            val lat = station.Latitud.replace(",", ".").toDoubleOrNull() ?: 0.0
            val lon = station.Longitud.replace(",", ".").toDoubleOrNull() ?: 0.0
            calculateDistance(latitude, longitude, lat, lon)
        }?.PrecioProducto?.replace(",", ".")?.toDoubleOrNull()
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "") // Elimina marcas diacríticas
    }
}

package com.project.atlas.facades

import Calories
import Diesel
import Electricity
import Petrol98
import com.project.atlas.interfaces.EnergyCostCalculatorInterface
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.models.RouteModel
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.ElectricityPriceService
import com.project.atlas.services.FuelPriceService
import kotlinx.coroutines.runBlocking

class EnergyCostCalculatorFacade(
    private val electricityPriceService: ElectricityPriceService = ElectricityPriceService,
    private val fuelPriceService: FuelPriceService = FuelPriceService(FuelPriceRepository())
) : EnergyCostCalculatorInterface {

    override suspend fun calculateCost(route: RouteModel): Double {
        val vehicle = route.vehicle
        return when (vehicle.energyType) {
            is Electricity -> calculateElectricityCost(route)
            is Calories -> calculateCaloriesCost(route)
            is Diesel -> calculateFuelCost(route)
            is Petrol98 -> calculateFuelCost(route)
            is Petrol95 -> calculateFuelCost(route)
            else -> throw IllegalArgumentException("Tipo de energía desconocido")
        }
    }

    private suspend fun calculateElectricityCost(route: RouteModel): Double {
        runBlocking {
            electricityPriceService.fetchPricesByHour()
        }
        val price = electricityPriceService.getPriceByHour()
        return route.vehicle.energyType!!.calculateCost(route.distance / 1000, route.vehicle.consumption!!, price)
    }

    private fun calculateCaloriesCost(route: RouteModel): Double {
        // Para calorías, simplemente calculamos usando la distancia y el consumo del vehículo
        return route.vehicle.energyType!!.calculateCost(route.distance / 1000, route.vehicle.consumption!!, 0.0)
    }

    private suspend fun calculateFuelCost(route: RouteModel): Double {
        var price = 0.0
        runBlocking {
            price = fuelPriceService.fetchFuelData(route.start.lat, route.start.lon, route.vehicle.energyType!!)!!
        }
        return route.vehicle.energyType!!.calculateCost(route.distance / 1000, route.vehicle.consumption!!, price)
        // Delegamos al servicio de precios de combustible
        //return fuelPriceService.calculateRoutePrice(route)
    }
}
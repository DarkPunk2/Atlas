package com.project.atlas.viewModels

import com.project.atlas.models.RouteModel
import com.project.atlas.services.ElectricityPriceService
import kotlinx.coroutines.runBlocking

class ElectricityServiceViewModel {
    private val electricityPriceService = ElectricityPriceService
    private fun getPriceByHour(): Double {
        updatePrices()
        return electricityPriceService.getPriceByHour()
    }
    private fun updatePrices(){
        runBlocking {
            electricityPriceService.fetchPricesByHour()
        }
    }
    fun calculateCost(route: RouteModel): Double{
        val price = getPriceByHour()
        val distance = route.distance
        val vehicle = route.vehicle
        return vehicle.energyType!!.calculateCost(distance/1000, vehicle.consumption!!, price)
    }
}
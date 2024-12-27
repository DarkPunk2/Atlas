package com.project.atlas.viewModels

import com.project.atlas.services.ElectricityPriceService
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

class ElectricityServiceViewModel {
    private val electricityPriceService = ElectricityPriceService()
    fun getPriceByHour(): Double {
        updatePrices()
        return electricityPriceService.getPriceByHour()
    }
    private fun updatePrices(){
        runBlocking {
            electricityPriceService.fetchPricesByHour()
        }
    }
}
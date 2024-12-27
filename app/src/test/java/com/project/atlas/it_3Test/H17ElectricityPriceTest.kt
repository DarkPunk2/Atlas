package com.project.atlas.it_3Test

import Calories
import Electricity
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.viewModels.ElectricityServiceViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H17ElectricityPriceTest {

    var vehicle = VehicleModel("Veh", VehicleType.Scooter, Electricity(), 5.9)
    var walk = VehicleModel("Walk", VehicleType.Walk, Calories(), 3.8)
    var cycle = VehicleModel("Cycle", VehicleType.Cycle, Calories(), 7.0)
    private lateinit var mockElectricityServiceViewModel : ElectricityServiceViewModel

    @Before
    fun setUp(){
        mockElectricityServiceViewModel = mock(ElectricityServiceViewModel::class.java)
    }

    @Test
    fun integrationTest1(){
        `when`(mockElectricityServiceViewModel.getPriceByHour()).thenReturn(175.0)
        var price = mockElectricityServiceViewModel.getPriceByHour()
        var total_cost = vehicle.energyType!!.calculateCost(500.0, vehicle.consumption!!, price)
        assertTrue(total_cost >= 0)
    }
}
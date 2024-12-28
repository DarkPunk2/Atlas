package com.project.atlas.it_3Test

import Calories
import Electricity
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
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

    private lateinit var mockElectricityServiceViewModel : ElectricityServiceViewModel

    @Before
    fun setUp(){
        mockElectricityServiceViewModel = mock(ElectricityServiceViewModel::class.java)
    }

    @Test
    fun integrationTest1(){
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val route = RouteModel("id",start,end, vehicle, RouteType.FASTER, 72000.0, 1.0, "rute", listOf(0.0))
        `when`(mockElectricityServiceViewModel.calculateCost(route)).thenReturn(175.0)

        var total_cost = mockElectricityServiceViewModel.calculateCost(route)
        assertTrue(total_cost >= 0)
    }
}
package com.project.atlas.it_3Test

import Calories
import Electricity
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import com.project.atlas.viewModels.ElectricityServiceViewModel
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before

import org.junit.Test


class H17ElectricityPriceTest {

    var vehicle = VehicleModel("Veh", VehicleType.Scooter, Electricity(), 5.9)
    var walk = VehicleModel("Walk", VehicleType.Walk, Calories(), 3.8)
    var cycle = VehicleModel("Cycle", VehicleType.Cycle, Calories(), 7.0)
    val electricityServiceViewModel : ElectricityServiceViewModel = ElectricityServiceViewModel()

    @Test
    fun acceptanceTest1(){
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val route = RouteModel("id",start,end, vehicle, RouteType.FASTER, 72000.0, 1.0, "rute", listOf(0.0))

        var total_cost = electricityServiceViewModel.calculateCost(route)
        assertTrue(total_cost >= 0)
    }

    @Test
    fun acceptanceTest2(){
        var total_cost = walk.energyType!!.calculateCost(20.0, walk.consumption!!, 0.0)
        assertTrue(total_cost == 1064.toDouble())
    }

    @Test
    fun acceptanceTest3(){
        var total_cost = cycle.energyType!!.calculateCost(20.0, cycle.consumption!!, 0.0)
        assertTrue(total_cost == 490.toDouble())
    }
}
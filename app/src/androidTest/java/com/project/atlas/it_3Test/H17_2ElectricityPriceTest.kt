package com.project.atlas.it_3Test

import Calories
import Electricity
import com.project.atlas.exceptions.InvalidDistanceException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.viewModels.ElectricityServiceViewModel
import junit.framework.TestCase.assertTrue

import org.junit.Test


class H17_2ElectricityPriceTest {

    var vehicle = VehicleModel("Veh", VehicleType.Scooter, Electricity(), 5.9)

    val electricityServiceViewModel : ElectricityServiceViewModel = ElectricityServiceViewModel()

    @Test
    fun acceptanceTest1(){
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val route = RouteModel("id",start,end,vehicle= vehicle, routeType = RouteType.FASTER, distance = 72000.0, duration = 1.0, rute= "rute", bbox = listOf(0.0))

        var total_cost = electricityServiceViewModel.calculateCost(route)
        assertTrue(total_cost >= 0)
    }

    @Test(expected = InvalidDistanceException::class)
    fun acceptanceTest2(){
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val route = RouteModel("id",start,end,vehicle= vehicle, routeType = RouteType.FASTER, distance = -72000.0, duration = 1.0, rute= "rute", bbox = listOf(0.0))

        var total_cost = electricityServiceViewModel.calculateCost(route)
        assertTrue(total_cost >= 0)
    }

}
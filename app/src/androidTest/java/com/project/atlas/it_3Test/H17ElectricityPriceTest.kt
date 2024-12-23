package com.project.atlas.it_3Test

import Calories
import Electricity
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import junit.framework.TestCase.assertTrue

import org.junit.Test


class H17ElectricityPriceTest {

    var vehicle = VehicleModel("Veh", VehicleType.Scooter, Electricity(), 5.9)
    var walk = VehicleModel("Walk", VehicleType.Walk, Calories(), 3.8)
    var cycle = VehicleModel("Cycle", VehicleType.Cycle, Calories(), 7.0)

    @Test
    fun acceptanceTest1(){
        var total_cost = vehicle.energyType!!.calculateCost(500.0, vehicle.consumption!!, 14.0)
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
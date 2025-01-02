package com.project.atlas.it_3Test

import Calories
import com.project.atlas.exceptions.InvalidDistanceException
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import junit.framework.TestCase.assertTrue
import org.junit.Test

class H17_1CaloriesCostTest {
    var walk = VehicleModel("Walk", VehicleType.Walk, Calories(), 3.8)
    var cycle = VehicleModel("Cycle", VehicleType.Cycle, Calories(), 7.0)

    @Test
    fun acceptanceTest1(){
        var total_cost = walk.energyType!!.calculateCost(20.0, walk.consumption!!, 0.0)
        assertTrue(total_cost == 1064.toDouble())
    }

    @Test(expected = InvalidDistanceException::class)
    fun acceptanceTest2(){
        var total_cost = walk.energyType!!.calculateCost(-20.0, walk.consumption!!, 0.0)
    }

    @Test
    fun acceptanceTest3(){
        var total_cost = cycle.energyType!!.calculateCost(20.0, cycle.consumption!!, 0.0)
        assertTrue(total_cost == 490.toDouble())
    }
}
package com.project.atlas.it_3Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H18RouteCheaperTest {
    private val routeService = RouteService(RouteDatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h18P1Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val route: RouteModel
        //When
        runBlocking {
            route = routeService.createRute(start, end, vehicle, RouteType.CHEAPER)
        }
        //Then
        assertTrue("Unexpected distance",route.distance in 67000.0..75000.0)
        assertTrue("Unexpected duration",route.duration in 3280.0..4720.0)
    }

    @Test(expected = InvalidRouteException::class)
    fun h18P4Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(40.724762, -73.994691, "Hotel", "New York")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        //When
        runBlocking {
            routeService.createRute(start, end, vehicle, RouteType.CHEAPER)
        }
        //Then
    }
}
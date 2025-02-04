package com.project.atlas.it_3Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.RouteNotFoundException
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
class H21DeleteRouteTest {
    private var routeService = RouteService(RouteDatabaseService().apply { setTestMode() })

    @Before
    fun setup(){
        UserModel.setMail("testDeleteRute@test.test")
    }
    @Test
    fun h21P1Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Casa","Castellon")
        val end = Location(39.479126, -0.342623,"Trabajo","Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val route: RouteModel
        runBlocking {
            route = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        runBlocking {
            routeService.addRoute(route)
        }
        //When
        val result: Boolean
        runBlocking {
            result = routeService.removeRoute(route.id)
        }
        //Then
        assertTrue("Rute is not deleted",result)
    }

    @Test(expected = RouteNotFoundException::class)
    fun h21P2Test(){
        //Given

        //When
        runBlocking {
            routeService.removeRoute("noRute")
        }
        //Then

    }
}
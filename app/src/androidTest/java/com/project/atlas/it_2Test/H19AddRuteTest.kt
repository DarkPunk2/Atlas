package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.FailDataBaseService
import com.project.atlas.services.RouteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H19AddRuteTest {
    private var routeService = RouteService(RouteDatabaseService().apply { setTestMode() })

    @Before
    fun setup(){
        UserModel.setMail("testAddRute@test.test")
    }
    @Test
    fun h19P1Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val route: RouteModel
        runBlocking {
            route = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        //When
        var result: Boolean
        runBlocking {
            result = routeService.addRoute(route)
        }
        //Then
        assertTrue("Route is not added",result)
        runBlocking {
            routeService.removeRoute(route.id)
        }
    }

    @Test(expected = ServiceNotAvailableException::class)
    fun h19P2Test(){
        //Given
        routeService = RouteService(FailDataBaseService())
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val routeModel: RouteModel
        runBlocking {
            routeModel = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        //When
        runBlocking {
            routeService.addRoute(routeModel)
        }
        //Then

    }
}
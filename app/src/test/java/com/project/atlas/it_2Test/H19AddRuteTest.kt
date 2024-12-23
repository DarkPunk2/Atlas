package com.project.atlas.it_2Test

import Diesel
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H19AddRuteTest {
    private lateinit var routeService: RouteService

    @Before
    fun setup(){
        UserModel.setMail("testAddRute@test.test")
    }
    @Test
    fun h19P1Test(): Unit = runBlocking{
        //Given
        val database = mock(RouteDatabaseService::class.java)
        `when`(database.checkForDuplicates(anyString(),anyString())).thenReturn(false)

        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val route = RouteModel("id",start,end,vehicle,RouteType.SHORTER,2.2,2.2,"5305873gg", listOf(0.9,0.3))

        `when`(database.add(route)).thenReturn(true)
        routeService = RouteService(database)
        //When
        val result: Boolean = routeService.addRoute(route)

        //Then
        assertTrue("Route is not added",result)
    }

    @Test(expected = ServiceNotAvailableException::class)
    fun h19P2Test(): Unit = runBlocking{
        //Given
        val database = mock(RouteDatabaseService::class.java)
        `when`(database.checkForDuplicates(anyString(),anyString())).thenReturn(false)

        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val route = RouteModel("id",start,end,vehicle,RouteType.SHORTER,2.2,2.2,"5305873gg", listOf(0.9,0.3))

        `when`(database.add(route)).thenAnswer {
            throw ServiceNotAvailableException("Service not available")
        }
        routeService = RouteService(database)
        //When
        routeService.addRoute(route)
        //Then

    }
}
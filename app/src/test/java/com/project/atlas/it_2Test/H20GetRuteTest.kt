package com.project.atlas.it_2Test

import Diesel
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.models.AuthState
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
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H20GetRuteTest {
    private lateinit var routeService: RouteService

    @Test
    fun h20P1Test()= runBlocking{
        //Given
        UserModel.setMail("testRute@test.test")
        UserModel.setAuthState(AuthState.Authenticated)

        val database = mock(RouteDatabaseService::class.java)

        val start = Location(39.992573, -0.064749,"Castellon","Castellon")
        val end = Location(39.479126, -0.342623,"Valencia","Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val route = RouteModel("id",start,end,vehicle,
            RouteType.SHORTER,2.2,2.2,"5305873gg", listOf(0.9,0.3))

        `when`(database.getAll()).thenReturn(listOf(route))
        routeService = RouteService(database)

        //When
        val result = routeService.getRoutes()

        //Then
        assertTrue("Incorrect number of values on the list",result.size == 1)
        assertTrue("Incorrect rute",result.get(0).id == "id")
    }

    @Test(expected = UserNotLoginException::class)
    fun h20P2Test(){
        //Given
        UserModel.setAuthState(AuthState.Unauthenticated)
        val database = mock(RouteDatabaseService::class.java)
        routeService = RouteService(database)
        //When
        runBlocking {
            routeService.getRoutes()
        }
        //Then

    }


}
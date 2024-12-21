package com.project.atlas.it_2Test

import Diesel
import com.project.atlas.apisRequest.ResponseDataForRoute
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.ApiClient
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H15RuteCreationTest {
    private lateinit var routeService: RouteService
    private val mockApiClient = mock(ApiClient::class.java)

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
        routeService = RouteService(mock(RouteDatabaseService::class.java))
        routeService.routeApi = mockApiClient
    }
    @Test
    fun h15P1Test() = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val rute: RouteModel

        val response = mock(ResponseDataForRoute::class.java)
        `when`(response.getDistance()).thenReturn(75000.0)
        `when`(response.getDuration()).thenReturn(3000.0)
        `when`(response.getRute()).thenReturn("Mocked Route")

        `when`(mockApiClient.fetchRoute(anyList(), anyString(),anyString())).thenReturn(response)
        //When
        runBlocking {
            rute = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        //Then
        assertTrue("Unexpected distance",rute.distance in 68000.0..82000.0)
        assertTrue("Unexpected duration",rute.duration in 2280.0..3720.0)
    }

    @Test(expected = InvalidRouteException::class)
    fun h15P4Test(): Unit = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749, "Castellon")
        val end = Location(40.724762, -73.994691, "New York")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        `when`(mockApiClient.fetchRoute(anyList(), anyString(), anyString())) .thenAnswer {
            throw InvalidRouteException("Invalid route")
        }
        //When
        runBlocking {
            routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        //Then
    }
}
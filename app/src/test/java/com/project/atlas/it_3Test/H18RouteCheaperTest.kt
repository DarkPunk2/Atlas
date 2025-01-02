package com.project.atlas.it_3Test

import Diesel
import com.project.atlas.apisRequest.RouteData
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.facades.EnergyCostCalculatorFacade
import com.project.atlas.interfaces.CalculateRoute
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


import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any


class  H18RouteCheaperTest {
    private lateinit var routeService: RouteService
    private val mockCalculateRoute = mock(CalculateRoute::class.java)
    private val mockCalculatePrice = mock(EnergyCostCalculatorFacade::class.java)

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
        routeService = RouteService(mock(RouteDatabaseService::class.java))
        routeService.routeApi = mockCalculateRoute
        routeService.costCalculator = mockCalculatePrice
    }
    @Test
    fun h18P1Test() = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749,"Castellon","Castellon")
        val end = Location(39.479126, -0.342623,"Valencia","Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val rute: RouteModel

        val response = RouteData(75000.0,3000.0,"Mocked Route", listOf(0.0))

        `when`(mockCalculatePrice.calculateCost(any())).thenReturn(40.0)
        `when`(mockCalculateRoute.fetchRoute(start, end,vehicle, RouteType.FASTER)).thenReturn(response)
        `when`(mockCalculateRoute.fetchRoute(start, end,vehicle, RouteType.SHORTER)).thenReturn(response)

        //When
        runBlocking {
            rute = routeService.createRute(start, end, vehicle, RouteType.CHEAPER)
        }
        //Then
        assertTrue("Unexpected distance",rute.distance in 68000.0..82000.0)
        assertTrue("Unexpected duration",rute.duration in 2280.0..3720.0)
    }

    @Test(expected = InvalidRouteException::class)
    fun h18P4Test(): Unit = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749, "Castellon","Castellon")
        val end = Location(40.724762, -73.994691, "New York","New York")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        `when`(mockCalculateRoute.fetchRoute(start, end,vehicle,RouteType.FASTER)) .thenAnswer {
            throw InvalidRouteException("Invalid route")
        }
        //When
        runBlocking {
            routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        //Then
    }
}
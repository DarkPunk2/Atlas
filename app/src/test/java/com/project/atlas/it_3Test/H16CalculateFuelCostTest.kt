package com.project.atlas.it_3Test

import Diesel
import com.project.atlas.apisRequest.ResponseDataForRoute
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.repository.FuelPriceRepository
import com.project.atlas.services.ApiClient
import com.project.atlas.services.FuelPriceService
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H16CalculateFuelCostTest {

    private lateinit var routeService: RouteService
    private lateinit var fuelPriceService: FuelPriceService
    private val mockApiClient = mock(ApiClient::class.java)

    @Before
    fun setup() {
        UserModel.setMail("testCalculateCost@test.test")
        routeService = RouteService(mock(RouteDatabaseService::class.java)).apply {
            routeApi = mockApiClient
        }
        fuelPriceService = FuelPriceService(mock(FuelPriceRepository::class.java))
    }


    @Test
    fun h16pa1Test(){ runBlocking {
        // Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)

        val response = mock(ResponseDataForRoute::class.java)
        `when`(response.getDistance()).thenReturn(75000.0)
        `when`(response.getDuration()).thenReturn(3000.0)
        `when`(response.getRute()).thenReturn("Mocked Route")

        `when`(mockApiClient.fetchRoute(anyList(), anyString(), anyString())).thenReturn(response)

        // Mocking the fuel price repository
        val mockedFuelPrice = 1.5 // Example fuel price per liter
        `when`(
            fuelPriceService.fetchFuelData(
                start.lat,
                start.lon,
                vehicle.energyType!!
            )
        ).thenReturn(mockedFuelPrice)

        // When
        val route = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        val calculatedPrice = fuelPriceService.calculateRoutePrice(route)

        // Then
        val expectedPrice = vehicle.energyType?.calculateCost(
            route.distance / 1000,
            mockedFuelPrice,
            vehicle.consumption!!
        )
        if (calculatedPrice != null && expectedPrice != null) {
            assertEquals(expectedPrice, calculatedPrice, 0.0001)
        }
    }

    }

    @Test(expected = InvalidRouteException::class) //Datos eroneos en la primera ubicacion
    fun h16pa3est(){
        //Given
        val start = Location(2000.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val rute: RouteModel
        val precioCombustible: Double?
        val precioRuta: Double?
        runBlocking {
            rute = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        runBlocking {
            routeService.addRoute(rute)
        }
        runBlocking {
            precioCombustible = fuelPriceService.fetchFuelData(start.lat,start.lon, vehicle.energyType!!)
        }

        //When
        runBlocking {
            precioRuta = fuelPriceService.calculateRoutePrice(rute)
        }

        //Then - salta la excepción
    }

    @Test(expected = InvalidRouteException::class) //Datos erroneos en la segunda ubicacion
    fun h16pa4Test(){
        //Given
        val start = Location(1.992573, -0.064749, "Casa", "Castellon")
        val end = Location(2000.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val rute: RouteModel
        val precioCombustible: Double?
        val precioRuta: Double?
        runBlocking {
            rute = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        runBlocking {
            routeService.addRoute(rute)
        }
        runBlocking {
            precioCombustible = fuelPriceService.fetchFuelData(start.lat,start.lon, vehicle.energyType!!)
        }

        //When
        runBlocking {
            precioRuta = fuelPriceService.calculateRoutePrice(rute)
        }

        //Then - salta la excepción
    }

}
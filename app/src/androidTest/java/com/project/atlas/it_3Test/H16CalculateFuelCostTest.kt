package com.project.atlas.it_3Test

import Diesel
import com.project.atlas.exceptions.InvalidRouteException
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.FuelPriceService
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class H16CalculateFuelCostTest {

    private var routeService = RouteService(RouteDatabaseService().apply { setTestMode() })
    private var fuelPriceService = FuelPriceService

    @Before
    fun setup(){
        UserModel.setMail("testCalculateCost@test.test")
    }

    @Test
    fun h16pa1Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val rute: RouteModel
        val precioCombustible: Double?
        val precioRuta: Double?
        runBlocking {
            rute = routeService.createRute(start, end, vehicle, RouteType.FASTER)
        }
        runBlocking {
            precioCombustible = fuelPriceService.fetchFuelData(start.lat,start.lon, vehicle.energyType!!)
        }

        //When
        runBlocking {
            precioRuta = fuelPriceService.calculateRoutePrice(rute)
        }

        //Then
        assertTrue(precioCombustible != null && precioRuta != null)

        val precioTeorico = vehicle.energyType?.calculateCost(rute.distance/1000, precioCombustible!!, vehicle.consumption!!)
        if (precioTeorico != null && precioRuta != null) {
            assertEquals(precioTeorico, precioRuta, 0.0001)
        }else{
            assertTrue("Prices can't be null",false)
        }

    }

    @Test(expected = InvalidRouteException::class) //Datos eroneos en la primera ubicacion
    fun h16pa3Test(){
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
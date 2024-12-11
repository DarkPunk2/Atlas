package com.project.atlas.it_2Test

import com.project.atlas.Exceptions.VehicleNotExistsException
import com.project.atlas.Interfaces.Petrol95
import com.project.atlas.Interfaces.VehicleInterface
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Models.VehicleType
import com.project.atlas.Services.VehicleDatabaseService
import com.project.atlas.Services.VehicleService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class H13VehicleDeleteTest {
    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        dbService.setTestMode()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1(){
        //Given - hay un vehículo en lista
        val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), 7.9)
        runBlocking {
            service.addVehicle("testVehicleDelete", vehicle)
        }
        //When - se elimina el vehículo
        runBlocking {
            service.deleteVehicle("testVehicleDelete", vehicle.alias!!)
        }
        //Then se devuelve lista de vehículos (sin vehículos dentro)
        var vehicleList : List<VehicleModel> = emptyList()
        runBlocking {
            vehicleList = service.listVehicle("testVehicleDelete")!!
        }
        assertTrue(vehicleList is List<VehicleModel> && vehicleList.isEmpty())
    }
    @Test(expected = VehicleNotExistsException::class)
    fun acceptanceTest2(){
        //Given - hay un vehículo en lista
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)

        runBlocking {
            service.addVehicle("testVehicleDelete", vehicle)
        }
        //When - se intenta eliminar un vehículo que no ha sido añadido
        val vehicleIncorrect = VehicleModel("Mi coche99", VehicleType.Car, Petrol95(), 7.9)
        runBlocking {
            service.deleteVehicle("testVehicleDelete", vehicleIncorrect.alias!!)
        }
        //Then - salta la excepción
    }
    @After
    fun deleteAddedVehicle() {
        runBlocking {
            service.deleteVehicle("testVehicleDelete", "Mi buga")
        }
    }
}
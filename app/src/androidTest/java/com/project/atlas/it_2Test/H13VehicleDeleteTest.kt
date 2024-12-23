package com.project.atlas.it_2Test

import com.project.atlas.exceptions.VehicleNotExistsException
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class H13VehicleDeleteTest {
    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    private var user:String = "testVehicleDelete"
    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1(){
        //Given - hay un vehículo en lista
        val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), 7.9)
        runBlocking {
            service.addVehicle(user, vehicle)
        }
        //When - se elimina el vehículo
        runBlocking {
            service.deleteVehicle(user, vehicle.alias!!)
        }
        //Then se devuelve lista de vehículos (sin vehículos dentro)
        var vehicleList : List<VehicleModel> = emptyList()
        runBlocking {
            vehicleList = service.listVehicle(user)!!
        }
        assertTrue(vehicleList is List<VehicleModel> && vehicleList.size <= 2)
    }
    @Test(expected = VehicleNotExistsException::class)
    fun acceptanceTest2(){
        //Given - hay un vehículo en lista
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)

        runBlocking {
            service.addVehicle(user, vehicle)
        }
        //When - se intenta eliminar un vehículo que no ha sido añadido
        val vehicleIncorrect = VehicleModel("Mi coche99", VehicleType.Car, Petrol95(), 7.9)
        runBlocking {
            service.deleteVehicle(user, vehicleIncorrect.alias!!)
        }
        //Then - salta la excepción
    }
    @After
    fun deleteVehicle(){
        runBlocking {
            service.deleteAll(user)
        }
    }
}
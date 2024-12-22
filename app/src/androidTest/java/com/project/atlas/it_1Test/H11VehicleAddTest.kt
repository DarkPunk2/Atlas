package com.project.atlas.it_1Test

import com.project.atlas.exceptions.VehicleNotExistsException
import com.project.atlas.interfaces.*
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleService
import com.project.atlas.services.VehicleDatabaseService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class H11VehicleAddTest {
    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    val user: String = "testVehicleAdd"

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1() {
        //Given - lista vacía
        runBlocking{
            assertTrue(service.listVehicle(user)!!.size<=2)
        }
        //When - se quiere añadir este vehículo
        val vehicle = VehicleModel("Mi coche",VehicleType.Car, Petrol95(), 7.9)
        //Then - se intenta añadir el vehículo
        runBlocking{
        assertTrue(service.addVehicle(user,vehicle))
        assertFalse(service.listVehicle(user)!!.size<=2)
        }
    }

    @Test
    fun acceptanceTest2(): Unit = runBlocking{
        //Given - lista no vacía
        val vehicle = VehicleModel("Mi buga",VehicleType.Car, Petrol95(), 7.9)
        service.addVehicle(user,vehicle)
        var initial_size = service.listVehicle(user)!!.size
        assertTrue(initial_size > 0)
        //When - se quiere añadir este vehículo
        val vehicleReapeted = VehicleModel("Mi buga",VehicleType.Car, Petrol95(), 7.9)
        service.addVehicle("testVehicleAdd",vehicleReapeted)
        //Then - el vehículo no se añade
        val list: List<VehicleModel>? = service.listVehicle(user)
        var final_size = list!!.size
        assertEquals(initial_size, final_size)
        assertEquals(list.first().alias, vehicleReapeted.alias)
    }
    @After
    fun deleteVehicle(){
        runBlocking {
            service.deleteAll(user)
        }
    }
}
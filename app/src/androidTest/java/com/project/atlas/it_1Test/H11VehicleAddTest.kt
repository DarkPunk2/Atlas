package com.project.atlas.it_1Test

import com.project.atlas.interfaces.*
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleService
import com.project.atlas.services.VehicleDatabaseService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class H11VehicleAddTest {
    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        dbService.setTestMode()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1() {
        //Given - lista vacía
        runBlocking{
            assertTrue(service.listVehicle("testVehicleAdd")!!.isEmpty())
        }
        //When - se quiere añadir este vehículo
        val vehicle = VehicleModel("Mi coche",VehicleType.Car, Petrol95(), 7.9)
        //Then - se intenta añadir el vehículo
        runBlocking{
        assertTrue(service.addVehicle("testVehicleAdd",vehicle))
        assertFalse(service.listVehicle("testVehicleAdd")!!.isEmpty())
        }
    }

    @Test
    fun acceptanceTest2() = runBlocking{
        //Given - lista no vacía
        val vehicle = VehicleModel("Mi buga",VehicleType.Car, Petrol95(), 7.9)
        service.addVehicle("testVehicleAdd",vehicle)
        var initial_size = service.listVehicle("testVehicleAdd")!!.size
        assertTrue(initial_size > 0)
        //When - se quiere añadir este vehículo
        val vehicleReapeted = VehicleModel("Mi buga",VehicleType.Car, Petrol95(), 7.9)
        service.addVehicle("testVehicleAdd",vehicleReapeted)
        //Then - el vehículo no se añade
        val list: List<VehicleModel>? = service.listVehicle("testVehicleAdd")
        var final_size = list!!.size
        assertEquals(initial_size, final_size)
        assertEquals(list.first().alias, vehicleReapeted.alias)
    }
}
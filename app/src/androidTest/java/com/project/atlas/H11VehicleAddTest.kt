package com.project.atlas

import com.project.atlas.Interfaces.*
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Services.VehicleService
import com.project.atlas.Services.VehicleDatabaseService
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
        dbService = VehicleDatabaseService();
        service = VehicleService(dbService);
    }

    @Test
    fun acceptanceTest1(){
        //Given - lista vacía
        assertTrue(service.listVehicle("testVehicleAdd")!!.isEmpty())
        //When - se quiere añadir este vehículo
        val vehicle = VehicleModel("Mi coche","Coche", Petrol95(), 7.9)
        //Then - se intenta añadir el vehículo
        assertTrue(service.addVehicle("testVehicleAdd",vehicle))
        assertFalse(service.listVehicle("testVehicleAdd")!!.isEmpty())
    }

    @Test
    fun acceptanceTest2(){
        //Given - lista no vacía
        val vehicle = VehicleModel("Mi buga","Coche", Petrol95(), 7.9)
        service.addVehicle("testVehicleAdd",vehicle)
        //When - se quiere añadir este vehículo
        val vehicleReapeted = VehicleModel("Mi buga","Coche", Petrol95(), 7.9)
        service.addVehicle("testVehicleAdd",vehicleReapeted)
        //Then - el vehículo no se añade
        val list: List<VehicleModel>? = service.listVehicle("testVehicleAdd")
        assertEquals(list!!.get(-1), vehicleReapeted)
    }
}
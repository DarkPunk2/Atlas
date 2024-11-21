package com.project.atlas

import com.project.atlas.Exceptions.vehicleWrongBusinessRulesException
import com.project.atlas.Interfaces.*
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Services.VehicleDatabaseService
import com.project.atlas.Services.VehicleService
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.BeforeEach

class H11VehicleAddTest {
    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    @BeforeEach
    fun setUp(){
        dbService = VehicleDatabaseService();
        service = VehicleService();
    }

    @Test
    fun acceptanceTest1(){
        val vehicle = VehicleModel("Mi coche","Coche", Petrol95(), 7.9)
        assertTrue(service.addVehicle("test@gmail.com",vehicle))
    }

    @Test
    fun acceptanceTest2(){
        val vehicle = VehicleModel("Mi coche","Coche", Petrol95(), 7.9)
        service.addVehicle("test@gmail.com",vehicle)
        assertTrue(service.checkEntry("test@gmail.com",vehicle))
    }

    //{"Nombre: Mi coche, Tipo: null, Energía: Gasolina 95, Consumo: 7.9 l/100Km"}
    @Test(expected = vehicleWrongBusinessRulesException::class)
    fun acceptanceTest3(){
        val vehicle = VehicleModel("Mi coche",null, Petrol95(), 7.9)
        service.addVehicle("test@gmail.com", vehicle)
    }
    @Test
    fun acceptanceTest4(){
        assertTrue(dbService.checkConnection())
    }
}
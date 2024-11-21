package com.project.atlas


import com.project.atlas.Interfaces.*
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Services.VehicleService
import com.project.atlas.Services.VehicleDatabaseService
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test



class H12VehicleListTest {

    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService();
        service = VehicleService(dbService);
    }

    @Test
    fun acceptanceTest1(){
        //Given - lista no vacía
        val vehicle = VehicleModel("Mi coche","Coche", Petrol95(), 7.9)
        service.addVehicle("testVehicleList",vehicle)
        //When - se solicita lista de vehículos
        val vehicleList : List<VehicleModel>? = service.listVehicle("testVehicleList")
        //Then se devuelve lista de vehículos (con vehículos dentro)
        assertTrue(vehicleList is List<VehicleModel> && !vehicleList.isEmpty())
    }

    @Test
    fun acceptanceTest2(){
        //Given - lista de vehículos vacía

        //When - se solicita la lista de vehículos
        val vehicleList : List<VehicleModel>? = service.listVehicle("emptyVehicleList")

        //Then se devuelve vacía lista de vehículos
        assertTrue(vehicleList is List<VehicleModel> && vehicleList.isEmpty())
    }


}
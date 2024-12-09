package com.project.atlas.it_1Test


import com.project.atlas.Interfaces.*
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Models.VehicleType
import com.project.atlas.Services.VehicleService
import com.project.atlas.Services.VehicleDatabaseService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test



class H12VehicleListTest {

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
        //Given - lista no vacía
        val vehicle = VehicleModel("Mi coche", VehicleType.Coche, Petrol95(), 7.9)
        runBlocking {
            service.addVehicle("testVehicleList",vehicle)
        }
        //When - se solicita lista de vehículos
        var vehicleList : List<VehicleModel> = emptyList()
        runBlocking {
             vehicleList = service.listVehicle("testVehicleList")!!
        }
        //Then se devuelve lista de vehículos (con vehículos dentro)
        assertTrue(vehicleList is List<VehicleModel> && !vehicleList.isEmpty())
    }

    @Test
    fun acceptanceTest2(){
        //Given - lista de vehículos vacía
        var vehicleList : List<VehicleModel>? = emptyList()
        //When - se solicita la lista de vehículos
        runBlocking {
             vehicleList = service.listVehicle("emptyVehicleList")
        }
        //Then se devuelve vacía lista de vehículos
        assertTrue(vehicleList is List<VehicleModel> && vehicleList!!.isEmpty())
    }


}
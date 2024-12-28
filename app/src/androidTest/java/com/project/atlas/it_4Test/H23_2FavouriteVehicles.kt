package com.project.atlas.it_4Test

import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class H23_2FavouriteVehicles {

    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    val user: String = "testFavouriteVehicle"

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1(){
        // Given - vehículo en lista y no establecido como favorito
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        assertFalse(vehicle.favourite)
        //When - se quiere marcar el vehículo como favorito para que figure en la bddd y el modelo
        vehicle.toggleFavourite()
        assertTrue(vehicle.favourite)
        runBlocking {
            service.updateVehicle(user,vehicle.alias!!,vehicle)
        }
        //Then - el vehículo queda marcado como favorito
        runBlocking {
            vehicleList = service.listVehicle(user)!!
        }
        assertTrue(vehicleList.get(0).favourite)
    }

    @Test
    fun acceptanceTest2(){
        // Given - vehículo en lista y establecido como favorito
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        vehicle.toggleFavourite()
        assertTrue(vehicle.favourite)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        assertTrue(vehicleList.get(0).favourite)
        //When - se quiere marcar el vehículo como no favorito (figurará en la bddd y el modelo)
        vehicle.toggleFavourite()
        assertFalse(vehicle.favourite)
        runBlocking {
            service.updateVehicle(user,vehicle.alias!!,vehicle)
        }
        //Then - el vehículo queda marcado como no favorito
        runBlocking {
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.get(0).favourite)
    }


    @Test
    fun acceptanceTest3(){
        // Given - vehículo en lista y no establecido como favorito
        val oldAlias = "Mi buga"
        val newAlias = "Mi coche"
        val vehicle = VehicleModel(oldAlias, VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        assertFalse(vehicle.favourite)
        //When - se quiere marcar el vehículo como favorito para que figure en la bddd y el modelo
        vehicle.toggleFavourite()
        assertTrue(vehicle.favourite)

        //se modifica el nombre del vehículo y se actualiza
        vehicle.alias = newAlias
        runBlocking {
            service.updateVehicle(user,oldAlias,vehicle)
        }
        //Then - el vehículo debe mantener su condición de favorito
        runBlocking {
            vehicleList = service.listVehicle(user)!!
        }
        assertTrue(vehicleList.get(0).favourite)
    }

}
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H23_2FavouriteVehicles {
    private lateinit var mockDbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    val user: String = "testFavouriteVehicle"

    @Before
    fun setUp(){
        mockDbService = mock(VehicleDatabaseService::class.java)
        service = VehicleService(mockDbService)
    }
    @Test
    fun integrationTest1(){
        // Given - vehículo en lista y no establecido como favorito
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            `when`(mockDbService.addVehicle(user, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        assertFalse(vehicle.favourite)
        //When - se quiere marcar el vehículo como favorito para que figure en la bddd y el modelo
        vehicle.toggleFavourite()
        assertTrue(vehicle.favourite)
        runBlocking {
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.updateVehicle(user,vehicle.alias!!,vehicle)
        }
        //Then - el vehículo queda marcado como favorito
        runBlocking {
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            vehicleList = service.listVehicle(user)!!
        }
        assertTrue(vehicleList.get(0).favourite)
    }
    @Test
    fun integrationTest2(){
        // Given - vehículo en lista y establecido como favorito
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        vehicle.toggleFavourite()
        assertTrue(vehicle.favourite)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        assertTrue(vehicleList.get(0).favourite)
        //When - se quiere marcar el vehículo como no favorito (figurará en la bddd y el modelo)
        vehicle.toggleFavourite()
        assertFalse(vehicle.favourite)
        runBlocking {
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.updateVehicle(user,vehicle.alias!!,vehicle)
        }
        //Then - el vehículo queda marcado como no favorito
        runBlocking {
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.get(0).favourite)
    }

    @Test
    fun integrationTest3(){
        // Given - vehículo en lista y no establecido como favorito
        val oldAlias = "Mi buga"
        val newAlias = "Mi coche"
        val vehicle = VehicleModel(oldAlias, VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

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
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.updateVehicle(user,oldAlias,vehicle)
        }
        //Then - el vehículo debe mantener su condición de favorito
        runBlocking {
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))
            
            vehicleList = service.listVehicle(user)!!
        }
        assertTrue(vehicleList.get(0).favourite)
    }

}
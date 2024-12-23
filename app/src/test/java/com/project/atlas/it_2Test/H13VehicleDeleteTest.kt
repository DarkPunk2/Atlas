package com.project.atlas.it_2Test

import com.project.atlas.exceptions.VehicleNotExistsException
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class H13VehicleDeleteTest {
    private lateinit var mockService: VehicleDatabaseService
    private lateinit var service: VehicleInterface
    val user: String = "testVehicleDelete"

    @Before
    fun setUp() {
        // Crear un mock del servicio de base de datos
        mockService = mock(VehicleDatabaseService::class.java)
        service = VehicleService(mockService)
    }

    @Test
    fun integrationTest1() = runBlocking {
        // Given - hay un vehículo en lista
        val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), 7.9)

        // Simular que el vehículo es añadido a la base de datos
        `when`(mockService.addVehicle(user, vehicle)).thenReturn(true)

        // Cuando - se añade el vehículo
        service.addVehicle(user, vehicle)

        // Simular que el vehículo es eliminado correctamente
        `when`(mockService.deleteVehicle(user, vehicle.alias!!)).thenReturn(true)

        // Cuando - se elimina el vehículo
        service.deleteVehicle(user, vehicle.alias!!)

        // Then - se devuelve lista de vehículos (sin vehículos dentro)
        `when`(mockService.checkForVehicles(user)).thenReturn(false)
        `when`(mockService.listVehicle(user)).thenReturn(emptyList())

        val resultList = service.listVehicle(user)
        assertTrue(resultList.isNullOrEmpty())
    }

    @Test(expected = VehicleNotExistsException::class)
    fun integrationTest2() {
        runBlocking {
            `when`(mockService.deleteVehicle("testUser", "nonExistentAlias"))
                .thenAnswer { throw VehicleNotExistsException("Vehicle not found") }
            //doAnswer(throw VehicleNotExistsException("Vehicle not found")).`when`(mockService).deleteVehicle("testUser", "nonExixtentAlias")

            // Llamar al método que va a lanzar la excepción
            service.deleteVehicle("testUser", "nonExistentAlias")
        }
    }
}

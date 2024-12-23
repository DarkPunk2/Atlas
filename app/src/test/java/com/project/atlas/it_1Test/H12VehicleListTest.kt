package com.project.atlas.it_1Test

import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H12VehicleListTest {
    private lateinit var mockService: VehicleDatabaseService
    private lateinit var service: VehicleInterface
    private val user: String = "testVehicleList"

    @Before
    fun setUp() {
        // Crear un mock de VehicleDatabaseService
        mockService = mock(VehicleDatabaseService::class.java)
        service = VehicleService(mockService)
    }
    @Test
    fun integrationTest1() {
        runBlocking {
            // Given - lista no vacía simulada
            val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), 7.9)

            // Simular que el vehículo es añadido correctamente
            `when`(mockService.addVehicle(user, vehicle)).thenReturn(true)

            // Simular que la lista ahora contiene el vehículo
            `when`(mockService.checkForVehicles(user)).thenReturn(false)
            `when`(mockService.listVehicle(user)).thenReturn(listOf(vehicle))

            // Agregar el vehículo
            service.addVehicle(user, vehicle)

            // When - solicitar lista de vehículos
            val vehicleList: List<VehicleModel>? = service.listVehicle(user)

            // Then - verificar que la lista contiene vehículos
            assertNotNull(vehicleList)
            assertTrue(vehicleList is List<VehicleModel> && vehicleList!!.isNotEmpty())
        }
    }
    @Test
    fun integrationTest2() {
        runBlocking {
            // Given - lista de vehículos vacía simulada
            `when`(mockService.checkForVehicles(user)).thenReturn(false)
            `when`(mockService.listVehicle("emptyVehicleList")).thenReturn(emptyList())

            // When - solicitar la lista de vehículos
            `when`(mockService.checkForVehicles(user)).thenReturn(false)
            val vehicleList: List<VehicleModel>? = mockService.listVehicle("emptyVehicleList")

            // Then - verificar que la lista está vacía
            assertNotNull(vehicleList)
            assertTrue(vehicleList is List<VehicleModel> && vehicleList!!.isEmpty())
        }
    }
}
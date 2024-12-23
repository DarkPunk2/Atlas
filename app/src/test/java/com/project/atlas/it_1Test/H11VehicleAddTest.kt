package com.project.atlas.it_1Test

import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H11VehicleAddTest {
        private lateinit var mockService: VehicleDatabaseService
        private lateinit var service: VehicleInterface

        val user: String = "testVehicleAdd"

        @Before
        fun setUp() {
            mockService = mock(VehicleDatabaseService::class.java)

            service = VehicleService(mockService)
        }

        @Test
        fun integrationTest1() {
            runBlocking {
                `when`(mockService.checkForVehicles(user)).thenReturn(false)
                `when`(mockService.listVehicle(user)).thenReturn(emptyList())

                assertTrue(service.listVehicle(user)!!.isEmpty())

                val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), 7.9)

                `when`(mockService.addVehicle(user, vehicle)).thenReturn(true)

                assertTrue(service.addVehicle(user, vehicle))

                `when`(mockService.listVehicle(user)).thenReturn(listOf(vehicle))
                assertFalse(service.listVehicle(user)!!.isEmpty())
            }
        }

        @Test
        fun integrationTest2() = runBlocking {
            val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
            `when`(mockService.checkForVehicles(user)).thenReturn(false)
            `when`(mockService.listVehicle(user)).thenReturn(listOf(vehicle))

            var initialSize = service.listVehicle(user)!!.size
            assertTrue(initialSize > 0)

            val vehicleRepeated = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
            `when`(mockService.addVehicle(user, vehicleRepeated)).thenReturn(false)

            assertFalse(service.addVehicle(user, vehicleRepeated))
            val list: List<VehicleModel>? = service.listVehicle(user)
            val finalSize = list!!.size

            assertEquals(initialSize, finalSize)
            assertEquals(list.first().alias, vehicleRepeated.alias)
        }
}
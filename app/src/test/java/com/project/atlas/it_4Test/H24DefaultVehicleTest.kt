package com.project.atlas.it_4Test

import Calories
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class H24DefaultVehicleTest {
    private lateinit var mockDbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    val user: String = "testDefaultVehicle"

    @Before
    fun setUp(){
        mockDbService = mock(VehicleDatabaseService::class.java)
        service = VehicleService(mockDbService)
    }

    @Test
    fun integrationTest1() = runBlocking {
        // Given - vehículo en lista y sin vehículo por defecto definido
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        val vehicleList = mutableListOf<VehicleModel>()

        `when`(mockDbService.addVehicle(user, vehicle)).thenReturn(true)
        `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
        `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

        service.addVehicle(user, vehicle)
        vehicleList.addAll(service.listVehicle(user)!!)

        assertFalse(vehicleList.isEmpty())

        // When - no hay vehículo por defecto definido
        `when`(mockDbService.getDefaultVehicle(user)).thenReturn(null)

        var default = service.getDefaultVehicle(user)
        assertNull(default)

        // When - se marca por defecto el vehículo "Mi buga"
        doReturn(null).`when`(mockDbService).getDefaultVehicle(user)
        `when`(mockDbService.setDefaultVehicle(user, vehicle)).thenReturn(true)

        val success = service.setDefaultVehicle(user, vehicle)
        assertTrue(success)

        doReturn(vehicle).`when`(mockDbService).getDefaultVehicle(user)
        // Then - el vehículo "Mi buga" es el vehículo por defecto
        default = service.getDefaultVehicle(user)
        assertEquals(vehicle.alias, default?.alias)
        assertEquals(vehicle.type, default?.type)
        assertEquals(vehicle.energyType?.typeName, default?.energyType?.typeName)
        assertEquals(vehicle.consumption, default?.consumption)
    }

    @Test
    fun integrationTest2(){
        // Given - vehículo en lista y sin vehículo por defecto definido (se pasa Walk por defecto)
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
        var default : VehicleModel? = null
        runBlocking {
            doReturn(null).`when`(mockDbService).getDefaultVehicle(user)
            `when`(mockDbService.setDefaultVehicle(user, vehicle)).thenReturn(true)
            assertTrue(service.setDefaultVehicle(user, vehicle))

            doReturn(vehicle).`when`(mockDbService).getDefaultVehicle(user)
            default = service.getDefaultVehicle(user)
        }

        assertTrue(default!!.alias.equals(vehicle.alias))
        assertTrue(default!!.type.equals((vehicle.type)))
        assertTrue(default!!.energyType!!.typeName.equals(vehicle.energyType!!.typeName))
        assertTrue(default!!.consumption == vehicle.consumption)
        // When - se marca por defecto el vehículo "Mi buga"
        //Then - la operación falla al intentar añadir al mismo vehículo
        runBlocking {
            `when`(mockDbService.setDefaultVehicle(user, vehicle)).thenReturn(false)
            assertFalse(service.setDefaultVehicle(user, vehicle))
        }
    }
    @Test
    fun integrationTest3(){
        // Given - vehículo en lista y sin vehículo por defecto definido (se pasa Walk por defecto)
        val oldAlias = "Mi buga"
        val newAlias = "Mi coche"
        var vehicle = VehicleModel(oldAlias, VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            `when`(mockDbService.addVehicle(user, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        var default : VehicleModel? = null
        runBlocking {
            doReturn(null).`when`(mockDbService).getDefaultVehicle(user)
            `when`(mockDbService.setDefaultVehicle(user, vehicle)).thenReturn(true)
            assertTrue(service.setDefaultVehicle(user, vehicle))

            doReturn(vehicle).`when`(mockDbService).getDefaultVehicle(user)
            default = service.getDefaultVehicle(user)
        }
        vehicle.alias = newAlias
        runBlocking {
            `when`(mockDbService.updateVehicle(user, vehicle.alias!!, vehicle)).thenReturn(true)
            `when`(mockDbService.checkForVehicles(user)).thenReturn(true)
            `when`(mockDbService.listVehicle(user)).thenReturn(listOf(vehicle))

            service.updateVehicle(user, oldAlias, vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        runBlocking {
            `when`(mockDbService.getDefaultVehicle(user)).thenReturn(vehicle)

            default = service.getDefaultVehicle(user)
        }
        assertTrue(default!!.alias == newAlias)

    }

}
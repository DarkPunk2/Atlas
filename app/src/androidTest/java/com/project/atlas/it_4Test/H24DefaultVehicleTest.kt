package com.project.atlas.it_4Test

import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class H24DefaultVehicleTest {

    private lateinit var dbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    val user: String = "testDefaultVehicle"

    @Before
    fun setUp(){
        dbService = VehicleDatabaseService()
        service = VehicleService(dbService)
    }

    @Test
    fun acceptanceTest1(){
        // Given - vehículo en lista y sin vehículo por defecto definido (se pasa Walk por defecto)
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        var default : VehicleModel? = null
        runBlocking {
            default = service.getDefaultVehicle(user)
        }
        assertNull(default)
        // When - se marca por defecto el vehículo "Mi buga"
        runBlocking {
            assertTrue(service.setDefaultVehicle(user, vehicle))
            default = service.getDefaultVehicle(user)
        }
        //Then - el vehículo "Mi buga" es el vehículo por defecto
        assertTrue(default!!.alias.equals(vehicle.alias))
        assertTrue(default!!.type.equals((vehicle.type)))
        assertTrue(default!!.energyType!!.typeName.equals(vehicle.energyType!!.typeName))
        assertTrue(default!!.consumption == vehicle.consumption)
    }

    @Test
    fun acceptanceTest2(){
        // Given - vehículo en lista
        val vehicle = VehicleModel("Mi buga", VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        //y se define por defecto
        var default : VehicleModel? = null
        runBlocking {
            assertTrue(service.setDefaultVehicle(user, vehicle))
            default = service.getDefaultVehicle(user)
        }
        assertTrue(default!!.alias.equals(vehicle.alias))
        assertTrue(default!!.type.equals((vehicle.type)))
        assertTrue(default!!.energyType!!.typeName.equals(vehicle.energyType!!.typeName))
        assertTrue(default!!.consumption == vehicle.consumption)
        // When - se marca por defecto el vehículo "Mi buga"
        //Then - la operación falla al intentar añadir al mismo vehículo
        runBlocking {
            assertFalse(service.setDefaultVehicle(user, vehicle))
        }
    }
    @Test
    fun acceptanceTest3(){
        // Given - vehículo en lista y sin vehículo por defecto definido (se pasa Walk por defecto)
        val oldAlias = "Mi buga"
        val newAlias = "Mi coche"
        var vehicle = VehicleModel(oldAlias, VehicleType.Car, Petrol95(), 7.9)
        var vehicleList: List<VehicleModel> = emptyList()
        runBlocking {
            service.addVehicle(user,vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        assertFalse(vehicleList.isEmpty())
        //y se define por defecto
        var default : VehicleModel? = null
        runBlocking {
            assertTrue(service.setDefaultVehicle(user, vehicle))
            default = service.getDefaultVehicle(user)
        }
        //se modifica el alias y se actualiza en bbdd
        vehicle.alias = newAlias
        runBlocking {
            service.updateVehicle(user, oldAlias, vehicle)
            vehicleList = service.listVehicle(user)!!
        }
        //se recupera el vehículo por defecto, que debe tener el nuevo alias
        runBlocking {
            default = service.getDefaultVehicle(user)
        }
        assertTrue(default!!.alias == newAlias)

    }
    @After
    fun deleteVehicle(){
        runBlocking {
            service.deleteAll(user)
            service.deleteDefaultVehicle(user)
        }
    }
}

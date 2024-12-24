package com.project.atlas.it_2Test

import Diesel
import Petrol98
import com.project.atlas.exceptions.VehicleNotExistsException
import com.project.atlas.exceptions.VehicleWrongBusinessRulesException
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.interfaces.VehicleInterface
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.VehicleDatabaseService
import com.project.atlas.services.VehicleService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class H14VehicleUpdateTest {
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
        //Given - hay un vehículo en lista
        val initial_consumption = 7.9
        val initial_energy = Petrol95()
        var vehicle = VehicleModel("Mi coche", VehicleType.Car, initial_energy, initial_consumption)
        runBlocking {
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        //When - se edita el vehículo y se actualiza en la base de datos
        vehicle.consumption=7.1
        vehicle.energyType=Diesel()
        runBlocking {
            service.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle)
        }
        vehicle.energyType=initial_energy
        vehicle.consumption = initial_consumption // retornamos el valor de inicio al objeto para realizar las comparaciones

        //Then - se recupera el vehículo modificado de la bbdd...
        var vehicle_updated:VehicleModel = VehicleModel("", VehicleType.Walk,Petrol98(),0.1) //uno por defecto
        runBlocking {
            vehicle_updated = service.getVehicle("testVehicleUpdate", vehicle.alias!!)
        }
        //Then - ...y se revisa que sólo se haya modificado el consumo
        assertTrue(vehicle.alias.equals(vehicle_updated.alias))
        assertTrue(vehicle.type.equals(vehicle_updated.type))
        assertTrue(vehicle.energyType!!.typeName != vehicle_updated.energyType!!.typeName)
        assertFalse(vehicle.consumption == vehicle_updated.consumption)
    }
    @After
    fun deleteVehicle(){
        runBlocking {
            try {
                service.deleteVehicle("testVehicleUpdate","Mi coche")
            }catch (e: VehicleNotExistsException){
                service.deleteVehicle("testVehicleUpdate","Mi buga")
            }
        }
    }
    @Test(expected = VehicleWrongBusinessRulesException::class)
    fun acceptanceTest2(){
        //Given - hay un vehículo en lista
        val initial_consumption = 7.9
        var vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), initial_consumption)
        runBlocking {
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        //When - se edita el vehículo y se actualiza en la base de datos
        vehicle.consumption=-7.1
        runBlocking {
            service.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle)
        }
        //Then - salta la excepción
    }
    @Test
    fun acceptanceTest3(){
        //Given - hay un vehículo en lista
        val initial_consumption = 7.9
        val initial_alias = "Mi coche"
        var vehicle = VehicleModel(initial_alias, VehicleType.Car, Petrol95(), initial_consumption)
        runBlocking {
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        //When - se edita el vehículo y se actualiza en la base de datos
        vehicle.alias = "Mi buga"
        vehicle.consumption=7.1
        runBlocking {
            service.updateVehicle("testVehicleUpdate", initial_alias, vehicle)
        }

        vehicle.consumption = initial_consumption // retornamos el valor de inicio al objeto para realizar las comparaciones
        var vehicle_updated:VehicleModel = VehicleModel("", VehicleType.Walk,Petrol98(),0.1) //uno por defecto
        runBlocking {
            vehicle_updated = service.getVehicle("testVehicleUpdate", vehicle.alias!!)
        }
        vehicle.alias = initial_alias // retornamos el valor de inicio al objeto para realizar las comparaciones
        //Then - ...y se revisa que sólo se haya modificado el consumo
        assertFalse(vehicle.alias.equals(vehicle_updated.alias))
        assertTrue(vehicle.type.equals(vehicle_updated.type))
        assertTrue(vehicle.energyType!!.typeName == vehicle_updated.energyType!!.typeName)
        assertFalse(vehicle.consumption == vehicle_updated.consumption)
    }

}
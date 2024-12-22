package com.project.atlas.it_2Test

import Diesel
import com.project.atlas.exceptions.VehicleWrongBusinessRulesException
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


class H14VehicleUpdateTest {
    private lateinit var mockDbService: VehicleDatabaseService
    private lateinit var service: VehicleInterface

    @Before
    fun setUp() {
        // Mockear la base de datos
        mockDbService = mock(VehicleDatabaseService::class.java)
        service = VehicleService(mockDbService) // Usar el servicio real, pero con el mock de la base de datos
    }
    @Test
    fun integrationTest1() {
        // Given - se simula que un vehículo está en la lista
        val initial_consumption = 7.9
        val initial_energy = Petrol95()
        val changed_consuption = 7.1
        val changed_energy = Diesel()
        val vehicle = VehicleModel("Mi coche", VehicleType.Car, initial_energy, initial_consumption)

        // Simulamos que el vehículo se añade a la base de datos
        runBlocking {
            `when`(mockDbService.addVehicle("testVehicleUpdate", vehicle)).thenReturn(true)
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        // When - se simula la edición del vehículo y actualización en la base de datos
        vehicle.consumption = changed_consuption
        vehicle.energyType = changed_energy

        runBlocking {
            // Simulamos que la actualización fue exitosa
            `when`(mockDbService.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle)).thenReturn(true)
            service.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle)
        }

        // Restauramos valores para la comparación posterior
        vehicle.energyType = initial_energy
        vehicle.consumption = initial_consumption

        // Then - verificamos que la base de datos haya sido consultada correctamente
        val vehicleUpdated = VehicleModel(vehicle.alias, vehicle.type,changed_energy,changed_consuption) // Inicialización por defecto
        runBlocking {
            // Simulamos que obtenemos el vehículo actualizado
            `when`(mockDbService.getVehicle("testVehicleUpdate", vehicle.alias!!)).thenReturn(vehicleUpdated)
            val retrievedVehicle = service.getVehicle("testVehicleUpdate", vehicle.alias!!)

            assertTrue(retrievedVehicle.alias == vehicle.alias)
            assertTrue(retrievedVehicle.type == vehicle.type)
            assertTrue(retrievedVehicle.energyType!!.typeName != vehicle.energyType!!.typeName) // Verifica que la energía ha cambiado
            assertFalse(retrievedVehicle.consumption == vehicle.consumption) // Verifica que el consumo ha cambiado
        }
    }
    @Test(expected = VehicleWrongBusinessRulesException::class)
    fun integrationTest2() {
        // Given - se simula que hay un vehículo en la base de datos
        val initial_consumption = 7.9
        val vehicle = VehicleModel("Mi coche", VehicleType.Car, Petrol95(), initial_consumption)

        runBlocking {
            // Simulamos que se agrega el vehículo correctamente
            `when`(mockDbService.addVehicle("testVehicleUpdate", vehicle)).thenReturn(true)
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        // When - se simula un intento de actualización con un consumo negativo
        vehicle.consumption = -7.1

        runBlocking {
            // Simulamos que la base de datos lanza una excepción al intentar actualizar
            `when`(mockDbService.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle)).thenAnswer{VehicleWrongBusinessRulesException::class.java}
            service.updateVehicle("testVehicleUpdate", vehicle.alias!!, vehicle) // Aquí debería lanzar la excepción
        }
    }

    @Test
    fun integrationTest3() {
        // Given - se simula que hay un vehículo en la base de datos
        val initial_consumption = 7.9
        val initial_alias = "Mi coche"
        val changed_consuption = 7.1
        val changed_alias = "Mi buga"
        var vehicle = VehicleModel(initial_alias, VehicleType.Car, Petrol95(), initial_consumption)

        runBlocking {
            // Simulamos que se agrega el vehículo correctamente
            `when`(mockDbService.addVehicle("testVehicleUpdate", vehicle)).thenReturn(true)
            service.addVehicle("testVehicleUpdate", vehicle)
        }

        // When - se intenta actualizar el vehículo con un alias distinto
        vehicle.alias = changed_alias
        vehicle.consumption = changed_consuption

        runBlocking {
            // Simulamos que la actualización fue exitosa
            `when`(mockDbService.updateVehicle("testVehicleUpdate", initial_alias, vehicle)).thenReturn(true)
            service.updateVehicle("testVehicleUpdate", initial_alias, vehicle)
        }

        // Restauramos los valores iniciales para la comparación
        vehicle.consumption = initial_consumption

        // Then - verificamos que la base de datos ha sido consultada correctamente
        val vehicleUpdated = VehicleModel(changed_alias, vehicle.type, vehicle.energyType, changed_consuption)
        runBlocking {
            `when`(mockDbService.getVehicle("testVehicleUpdate", vehicle.alias!!)).thenReturn(vehicleUpdated) // Simulamos que obtenemos el vehículo actualizado
            val retrievedVehicle = service.getVehicle("testVehicleUpdate", vehicle.alias!!)

            vehicle.alias = initial_alias // Restauramos el alias original para la comparación
            assertFalse(retrievedVehicle.alias == vehicle.alias) // Verifica que el alias cambió
            assertTrue(retrievedVehicle.type == vehicle.type) // Verifica que el tipo no ha cambiado
            assertTrue(retrievedVehicle.energyType!!.typeName == vehicle.energyType!!.typeName) // Verifica que el tipo de energía no ha cambiado
            assertFalse(retrievedVehicle.consumption == vehicle.consumption) // Verifica que el consumo ha cambiado
        }
    }

}
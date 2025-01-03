package com.project.atlas.it_5Test

import Diesel
import com.project.atlas.models.*
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.`when`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class H23_3FavouriteRouteTest {
    private lateinit var routeService: RouteService
    private lateinit var database: RouteDatabaseService

    @Before
    fun setup() {
        // Crear un mock para RouteDatabaseService
        database = mock(RouteDatabaseService::class.java)
        routeService = RouteService(database)

        // Configurar UserModel
        UserModel.setMail("testFavoriteRoute@test.test")
        UserModel.setAuthState(AuthState.Authenticated)
    }

    @Test
    fun createAndMarkFavoriteRouteTest(): Unit = runBlocking {
        // Given
        val start = Location(39.992573, -0.064749, "Casa", "Castellon")
        val end = Location(39.479126, -0.342623, "Trabajo", "Valencia")

        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val vehicle2 = VehicleModel("Moto", VehicleType.Bike, Diesel(), 4.0)
        val vehicle3 = VehicleModel("Patin", VehicleType.Scooter, Diesel(), 4.0)

        val route1 = RouteModel("1", start, end, vehicle, RouteType.FASTER, 10.0, 15.0, "route1", emptyList(), null)
        val route2 = RouteModel("2", start, end, vehicle2, RouteType.FASTER, 8.0, 12.0, "route2", emptyList(), null)
        val route3 = RouteModel("3", start, end, vehicle3, RouteType.FASTER, 5.0, 8.0, "route3", emptyList(), null)

        // Configurar mocks para crear y obtener rutas
        `when`(database.update(org.mockito.kotlin.any())).thenReturn(true)


        // When // Marcamos la ruta 1 como favorita
        val updatedRoute1 = route1.copy(isFavorite = !route1.isFavorite)
        val updated = routeService.updateRoute(updatedRoute1)

        `when`(database.getAll()).thenReturn(listOf(updatedRoute1, route2, route3))

        // Then
        val routes = routeService.getRoutes()
        val favoriteRoute = routes.find { it.id == updatedRoute1.id }
        assertTrue("Route should be updated successfully", updated)
        assertTrue("The route should be found in the list", favoriteRoute != null)
        assertEquals("The route should be marked as favorite", true, favoriteRoute?.isFavorite)
        assertEquals("The route should appear on top of the list", true, routes.first().isFavorite)

        // Verificar interacciones
        verify(database, atLeastOnce()).getAll()
    }
}

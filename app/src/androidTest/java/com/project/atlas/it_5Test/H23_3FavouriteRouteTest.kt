package com.project.atlas.it_5Test

import Diesel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.models.AuthState
import com.project.atlas.models.Location
import com.project.atlas.models.RouteModel
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue

class H23_3FavouriteRouteTest {
    private val routeService = RouteService(RouteDatabaseService())

    @Before
    fun setup() {
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

        var route1: RouteModel
        var route2: RouteModel
        var route3: RouteModel


        runBlocking {
            route1 = routeService.createRute(start, end, vehicle, RouteType.FASTER)
            route2 = routeService.createRute(start, end, vehicle2, RouteType.FASTER)
            route3 = routeService.createRute(start, end, vehicle3, RouteType.FASTER)
        }

        // When //Marcamos la ruta 1 como favorita

        route1 = route1.copy(isFavorite = !route1.isFavorite)
        var updated = false
        runBlocking {
            updated = routeService.updateRoute(route1)
        }


        // Then //Update devuelve true, la lsta de rutas contiene la nueva ruta, se marca como favorita y aparece la primera de la lista
        val routes: List<RouteModel>
        runBlocking {
            routes = routeService.getRoutes()
        }
        val favoriteRoute = routes.find { it.id == route1.id }
        assertTrue("Route should be updated successfully", updated)
        assertTrue("The route should be found in the list", favoriteRoute != null)
        assertEquals("The route should be marked as favorite", true, favoriteRoute?.isFavorite)
        assertEquals("The route should appear on top of the list", true, routes.first().isFavorite)
    }

    @After
    fun cleanup() {
        FirebaseFirestore.getInstance().collection("users").document(UserModel.eMail).delete()
    }
}
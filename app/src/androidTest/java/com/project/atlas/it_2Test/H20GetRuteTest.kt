package com.project.atlas.it_2Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.models.AuthState
import com.project.atlas.models.RouteModel
import com.project.atlas.models.UserModel
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H20GetRuteTest {
    private var routeService = RouteService(RouteDatabaseService())


    @Test
    fun h20P1Test(){
        //Given
        UserModel.setMail("testRute@test.test")
        UserModel.setAuthState(AuthState.Authenticated)

        //When
        val result: List<RouteModel>
        runBlocking {
            result = routeService.getRoutes()
        }
        //Then
        assertTrue("Incorrect number of values on the list",result.size == 1)
        assertTrue("Incorrect rute",result.get(0).id == "8860fca0-5df8-4739-b67d-a409ad29a6f7")
    }

    @Test(expected = UserNotLoginException::class)
    fun h20P2Test(){
        //Given
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        val result: List<RouteModel>
        runBlocking {
            result = routeService.getRoutes()
        }
        //Then

    }


}
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
        assertTrue("Incorrect route", result[0].id == "c4cd54b6-63f4-4344-a662-7e0e296a44cd")
    }

    @Test(expected = UserNotLoginException::class)
    fun h20P2Test(){
        //Given
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        runBlocking {
            routeService.getRoutes()
        }
        //Then

    }


}
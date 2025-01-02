package com.project.atlas.it_5Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.RouteTypeAlreadyAssignedException
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.services.routeServicies.RouteDatabaseService
import com.project.atlas.services.routeServicies.RouteService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H25DefaultRuteTypeTest {
    private val routeService = RouteService(RouteDatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRuteType@test.test")
    }
    @Test
    fun h25P1Test(): Unit = runBlocking{
        //Given

        //When
        val result = routeService.addDefaultRouteType(RouteType.SHORTER)
        //Then
        assertTrue("RouteType not asigned",result)
        assertEquals(RouteType.SHORTER, routeService.getDefaultRouteType())
    }
    @Test
    fun h25P2Test(): Unit = runBlocking{
        //Given
        routeService.addDefaultRouteType(RouteType.SHORTER)
        //When
        val result = routeService.addDefaultRouteType(RouteType.FASTER)
        //Then
        assertTrue("RouteType not asigned",result)
        assertEquals(RouteType.FASTER, routeService.getDefaultRouteType())
    }
    @Test(expected= RouteTypeAlreadyAssignedException::class)
    fun h25P3Test(): Unit = runBlocking{
        //Given
        routeService.addDefaultRouteType(RouteType.FASTER)
        //When
        routeService.addDefaultRouteType(RouteType.FASTER)
        //Then
    }
    @After
    fun cleanup(){
        FirebaseFirestore.getInstance().collection("users").document(UserModel.eMail).delete()
    }


}
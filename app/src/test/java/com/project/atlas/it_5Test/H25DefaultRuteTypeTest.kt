package com.project.atlas.it_5Test

import com.project.atlas.exceptions.RouteTypeAlreadyAssignedException
import com.project.atlas.models.RouteType
import com.project.atlas.models.UserModel
import com.project.atlas.services.RouteDatabaseService
import com.project.atlas.services.RouteService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H25DefaultRuteTypeTest {
    private lateinit var routeService: RouteService

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h25P1Test(): Unit = runBlocking{
        //Given
        val database = mock(RouteDatabaseService::class.java)
        `when`(database.addDefaultRouteType(RouteType.SHORTER)).thenReturn(true)
        `when`(database.getDefaultRouteType()).thenAnswer{throw NoSuchElementException("Not stored")}
        routeService = RouteService(database)
        //When
        val result = routeService.addDefaultRouteType(RouteType.SHORTER)
        //Then
        assertTrue("RouteType not asigned",result)
    }
    @Test
    fun h25P2Test(): Unit = runBlocking{
        //Given
        val database = mock(RouteDatabaseService::class.java)
        `when`(database.addDefaultRouteType(RouteType.FASTER)).thenReturn(true)
        `when`(database.getDefaultRouteType()).thenReturn(RouteType.SHORTER)
        routeService = RouteService(database)
        //When
        val result = routeService.addDefaultRouteType(RouteType.FASTER)
        //Then
        assertTrue("RouteType not asigned",result)
    }
    @Test(expected= RouteTypeAlreadyAssignedException::class)
    fun h25P3Test(): Unit = runBlocking{
        //Given
        val database = mock(RouteDatabaseService::class.java)
        `when`(database.addDefaultRouteType(RouteType.SHORTER)).thenReturn(false)
        `when`(database.getDefaultRouteType()).thenReturn(RouteType.SHORTER)
        routeService = RouteService(database)
        //When
        routeService.addDefaultRouteType(RouteType.SHORTER)
        //Then
    }


}
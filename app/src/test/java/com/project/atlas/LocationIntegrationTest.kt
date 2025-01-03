package com.project.atlas

import com.project.atlas.interfaces.LocationRepositoryInterface
import com.project.atlas.models.Location
import com.project.atlas.services.OpenRouteServiceAPI
import com.project.atlas.viewModels.LocationsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class LocationIntegrationTest {
    private lateinit var mockRepository: LocationRepositoryInterface
    private lateinit var locationsViewModel: LocationsViewModel
    private lateinit var mockApiClient: OpenRouteServiceAPI

    val coroutineTestWaitTime: Long = 100

    @Before
    fun startup() {
        mockApiClient = mock(OpenRouteServiceAPI::class.java)
        mockRepository = mock(LocationRepositoryInterface::class.java)

        locationsViewModel = LocationsViewModel(mockRepository)
        locationsViewModel.locationsApi = mockApiClient
    }

    @Test
    fun H7P1Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")

        //When
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        delay(coroutineTestWaitTime)

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 1)
        assertEquals(locationsViewModel.getLocation(0).alias, "Parque")
    }

    @Test(expected = IllegalArgumentException::class)
    fun H7P5Test() {
        //Given

        //When
        val location = Location(100.0, 100.0, "Parque", "Castellón")
        locationsViewModel.addLocation(location)

        //Then
    }

    @Test
    fun H8P1Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")

        //When
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Barcelona")

        locationsViewModel.addLocation(41.0, 1.0, "Museo")

        delay(coroutineTestWaitTime)

        //Then
        assertEquals(locationsViewModel.getAllLocations().size, 2)
    }

    @Test
    fun H8P2Test() {
        //Given

        //When

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 0)
    }

    @Test
    fun H9P1Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")

        //When
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        delay(coroutineTestWaitTime)

        val location = locationsViewModel.getLocation(0)
        locationsViewModel.updateLocation(location, "Parque Actualizado")

        //Then
        assertEquals(locationsViewModel.getLocation(0).alias, "Parque Actualizado")
    }

    @Test(expected = IllegalArgumentException::class)
    fun H9P2Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")

        //When
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        delay(coroutineTestWaitTime)

        val location = locationsViewModel.getLocation(0)
        locationsViewModel.updateLocation(location, 100.0, 100.0)

        //Then
    }

    @Test
    fun H10P1Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        delay(coroutineTestWaitTime)

        val location = locationsViewModel.getLocation(0)

        //When
        locationsViewModel.removeLocation(location)

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 0)
    }

    @Test(expected = IllegalStateException::class)
    fun H10P2Test() {
        //Given
        val location = Location(40.0, 0.0, "Parque", "Castellón")

        //When
        locationsViewModel.removeLocation(location)

        //Then
    }

    @Test //Make location favourite correctly
    fun H23_1P1Test() = runBlocking {
        //Given
        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Castellon")
        locationsViewModel.addLocation(45.0, 25.0, "Parque")

        `when`(mockApiClient.fetchToponymByLatLong(any(), any(), any())).thenReturn("Barcelona")
        locationsViewModel.addLocation(41.0, 1.0, "Museo")

        delay(coroutineTestWaitTime)

        val location1 = locationsViewModel.getLocation(0)
        val location2 = locationsViewModel.getLocation(1)

        //When
        locationsViewModel.changeFavourite(location2)

        //Then
        assertEquals(locationsViewModel.getLocation(1), location1)
        assertEquals(locationsViewModel.getLocation(0), location2)
        assertEquals(location2.isFavourite, true)
    }

    //Make a location that is not in list favourite
    @Test(expected = IllegalArgumentException::class)
    fun H23_1P3Test() {
        //Given
        val location1 = Location(40.0, 0.0, "Parque", "Castellón")

        //When
        locationsViewModel.changeFavourite(location1)

        //Then
    }
}
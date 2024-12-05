package com.project.atlas

import com.project.atlas.models.Location
import com.project.atlas.viewModels.LocationsViewModel

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue

class LocationsTest {

    private lateinit var locationsViewModel: LocationsViewModel

    @Before
    fun startup(){
        locationsViewModel = LocationsViewModel()
    }

    @Test
    fun H7P1Test(){
        //Given


        //When
        val location = Location(45.0, 25.0, "Parque")
        locationsViewModel.addLocation(location)

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 1)
        assertEquals(locationsViewModel.getAllLocations().get(0), location)
    }

    @Test
    fun H7P5Test(){
        //Given

        //When
        val location = Location(100.0, 100.0, "Parque")

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 0)
    }

    @Test
    fun H8P1Test(){
        //Given

        //When
        val location1 = Location(40.0, 0.0, "Parque")
        locationsViewModel.addLocation(location1)

        val location2 = Location(41.0, 1.0, "Museo")
        locationsViewModel.addLocation(location2)

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 2)
        assertTrue(locationsViewModel.getAllLocations().contains(location1))
        assertTrue(locationsViewModel.getAllLocations().contains(location2))
    }

    @Test
    fun H8P2Test(){
        //Given

        //When

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 0)
    }

    @Test
    fun H10P1Test(){
        //Given
        val location1 = Location(40.0, 0.0, "Parque")
        locationsViewModel.addLocation(location1)
        //When
        locationsViewModel.removeLocation(location1)

        //Then
        assertEquals(locationsViewModel.getNumLocations(), 0)
    }

    @Test(expected = IllegalStateException::class)
    fun H10P2Test(){
        //Given
        val location1 = Location(40.0, 0.0, "Parque")

        //When
        locationsViewModel.removeLocation(location1)

        //Then
    }
}
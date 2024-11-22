package com.project.atlas

import com.project.atlas.Models.Coordinates
import com.project.atlas.Models.Location
import com.project.atlas.ViewModels.LocationViewModel

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue

class H8GetLocationsTest {

    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun startup(){
        locationViewModel = LocationViewModel()
    }

    @Test
    fun H8P1Test(){
        //Given

        //When
        val coord1 = Coordinates(40.0, 0.0)
        val location1 = Location(coord1, "Parque")
        locationViewModel.addLocation(location1)

        val coord2 = Coordinates(41.0, 1.0)
        val location2 = Location(coord2, "Museo")
        locationViewModel.addLocation(location2)

        //Then
        assertEquals(locationViewModel.getNumLocations(), 2)
        assertTrue(locationViewModel.getAllLocations().contains(location1))
        assertTrue(locationViewModel.getAllLocations().contains(location2))
    }

    @Test
    fun H8P2Test(){
        //Given

        //When

        //Then
        assertThrows(IllegalStateException::class.java) {
            locationViewModel.getAllLocations()
        }
    }
}
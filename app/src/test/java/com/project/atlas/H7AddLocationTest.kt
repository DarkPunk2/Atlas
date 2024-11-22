package com.project.atlas

import com.project.atlas.Models.Coordinates
import com.project.atlas.Models.Location
import com.project.atlas.ViewModels.LocationViewModel

import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows

class H7AddLocationTest {

    private lateinit var locationViewModel: LocationViewModel

    @Before
    fun startup(){
        locationViewModel = LocationViewModel()
    }

    @Test
    fun H7P1Test(){
        //Given


        //When
        val coord = Coordinates(45.0, 25.0)
        val location = Location(coord, "Parque")
        locationViewModel.addLocation(location)

        //Then
        assertEquals(locationViewModel.getNumLocations(), 1)
        assertEquals(locationViewModel.getLocation(0), location)
    }

    @Test
    fun H7P2Test(){
        //Given

        //When
        val coord = Coordinates(45.0, 25.0)
        val location = Location(coord, "")

        //Then
        assertThrows(IllegalArgumentException::class.java) {
            locationViewModel.addLocation(location)
        }
    }
}
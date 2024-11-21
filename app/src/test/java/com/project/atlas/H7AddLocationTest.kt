package com.project.atlas

import com.project.atlas.Models.Coordinates
import com.project.atlas.Models.Location
import com.project.atlas.Services.LocationRepository
import com.project.atlas.ViewModels.LocationViewModel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class H7AddLocationTest {

    private lateinit var locManager: LocationViewModel
    private lateinit var locRepository: LocationRepository

    @BeforeEach
    fun startup(){
        locManager = LocationViewModel()
        locRepository = LocationRepository()
    }

    @Test
    fun H7P1Test(){
        //Given


        //When
        val coord: Coordinates = Coordinates(45.0, 25.0)
        val location: Location = Location(coord, "Parque")
        locManager.addLocation(location);

        //Then
        assertEquals(locRepository.getNumLocations(), 1)
        assertEquals(locRepository.getLocation(0), location)
    }

    @Test
    fun H7P2Test(){
        //Given

        //When
        val coord: Coordinates = Coordinates(45.0, 25.0)
        val location: Location = Location(coord, "")

        //Then
        assertThrows<IllegalArgumentException> { locManager.addLocation(location) }
    }
}
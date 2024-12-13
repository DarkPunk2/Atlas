package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.InvalidRuteException
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.RuteDatabaseService
import com.project.atlas.services.RuteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H15RuteCreationTest {
    private val ruteService = RuteService(RuteDatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h15P1Test(){
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val rute: RuteModel
        //When
        runBlocking {
            rute = ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //Then
        assertTrue("Unexpected distance",rute.distance in 68000.0..82000.0)
        assertTrue("Unexpected duration",rute.duration in 2280.0..3720.0)
    }

    @Test(expected = InvalidRuteException::class)
    fun h15P4Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Castellon")
        val end = Location(40.724762, -73.994691, "New York")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        //When
        runBlocking {
            ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //Then
    }
}
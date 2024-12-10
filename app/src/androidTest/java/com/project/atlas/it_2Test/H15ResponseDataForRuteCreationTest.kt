package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.UnreachableLocationException
import com.project.atlas.models.Location
import com.project.atlas.apisRequest.ResponseDataForRute
import com.project.atlas.apisRequest.RuteType
import com.project.atlas.models.RuteModel
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.DatabaseService
import com.project.atlas.services.RuteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H15ResponseDataForRuteCreationTest {
    private val ruteService = RuteService(DatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h15P1Test(){
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche","driving-car", Diesel(), 4.0)
        val rute: RuteModel
        //When
        runBlocking {
            rute = ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //Then
        assertTrue("Unexpected distance",rute.distance in 68000.0..82000.0)
        assertTrue("Unexpected duration",rute.duration in 2280.0..3720.0)
    }

    @Test(expected = UnreachableLocationException::class)
    fun h15P4Test(){
        //Given
        val start = Location(39.992573, -0.064749, "Castellon")
        val end = Location(40.724762, -73.994691, "New York")
        val vehicle = VehicleModel("Coche", "driving-car", Diesel(), 4.0)
        //When
        runBlocking {
            ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //Then
    }
}
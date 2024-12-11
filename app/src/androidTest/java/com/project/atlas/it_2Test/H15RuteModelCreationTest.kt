package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.UnreachableLocationException
import com.project.atlas.models.Location
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.DatabaseService
import com.project.atlas.services.RuteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H15RuteModelCreationTest {
    private val ruteService = RuteService(DatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h15P1Test() = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        //When
        val rute = ruteService.createRute(start,end,vehicle,RuteType.FASTER)
        //Then
        assertTrue("Unexpected distance",rute.getDistance() in 68.0..82.0)
        assertTrue("Unexpected duration",rute.getDuration() in 38..72)
    }

    @Test(expected = UnreachableLocationException::class)
    fun h15P4Test() = runBlocking {
        //Given
        val start = Location(39.992573, -0.064749, "Castellon")
        val end = Location(40.724762, -73.994691, "New York")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        //When
        ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        //Then
    }
}
package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.apisRequest.Route
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.models.Location
import com.project.atlas.models.RuteModel
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.services.RuteDatabaseService
import com.project.atlas.services.FailDataBaseService
import com.project.atlas.services.RuteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H19AddRuteTest {
    private var ruteService = RuteService(RuteDatabaseService().apply { setTestMode() })

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h19P1Test(){
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche", VehicleType.Car, Diesel(), 4.0)
        val rute: RuteModel
        runBlocking {
            rute = ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //When
        val result: Boolean
        runBlocking {
            result = ruteService.addRute(rute)
        }
        //Then
        assertTrue("Rute is not added",result)
    }

    @Test(expected = ServiceNotAvailableException::class)
    fun h19P2Test(){
        //Given
        ruteService = RuteService(FailDataBaseService())
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche",VehicleType.Car, Diesel(), 4.0)
        val rute: RuteModel
        runBlocking {
            rute = ruteService.createRute(start, end, vehicle, RuteType.FASTER)
        }
        //When
        runBlocking {
            ruteService.addRute(rute)
        }
        //Then

    }
}
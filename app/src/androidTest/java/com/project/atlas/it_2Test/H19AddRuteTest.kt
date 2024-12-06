package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.models.Location
import com.project.atlas.models.RuteType
import com.project.atlas.models.UserModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.services.DatabaseService
import com.project.atlas.services.FailDataBaseService
import com.project.atlas.services.RuteService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H19AddRuteTest {
    private var ruteService = RuteService(DatabaseService())

    @Before
    fun setup(){
        UserModel.setMail("testRute@test.test")
    }
    @Test
    fun h19P1Test() = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche","Coche", Diesel(), 4.0)
        val rute = ruteService.createRute(start,end,vehicle,RuteType.FASTER)
        //When
        val result = ruteService.addRute(rute)
        //Then
        assertTrue("Rute is not added",result)
    }

    @Test(expected = ServiceNotAvailableException::class)
    fun h19P2Test() = runBlocking{
        //Given
        ruteService = RuteService(FailDataBaseService())
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche","Coche", Diesel(), 4.0)
        val rute = ruteService.createRute(start,end,vehicle,RuteType.FASTER)
        //When
        ruteService.addRute(rute)
        //Then

    }


}
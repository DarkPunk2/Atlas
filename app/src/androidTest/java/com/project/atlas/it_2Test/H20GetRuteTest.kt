package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.UserNotLoginException
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
class H20GetRuteTest {
    private val ruteService = RuteService(DatabaseService())


    @Test
    fun h20P1Test() = runBlocking{
        //Given
        UserModel.setMail("testRute@test.test")

        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche","Coche", Diesel(), 4.0)
        val rute = ruteService.createRute(start,end,vehicle,RuteType.FASTER)
        //When
        ruteService.addRute(rute)
        //Then
        assertTrue("No",ruteService.getRutes().size == 1)
    }

    @Test(expected = UserNotLoginException::class)
    fun h20P2Test() = runBlocking{
        //Given
        val start = Location(39.992573, -0.064749,"Castellon")
        val end = Location(39.479126, -0.342623,"Valencia")
        val vehicle = VehicleModel("Coche","Coche", Diesel(), 4.0)
        val rute = ruteService.createRute(start,end,vehicle,RuteType.FASTER)
        //When
        ruteService.addRute(rute)
        //Then

    }


}
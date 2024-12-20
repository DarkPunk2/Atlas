package com.project.atlas.it_2Test

import Diesel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.UserNotLoginException
import com.project.atlas.models.AuthState
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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H20GetRuteTest {
    private var ruteService = RuteService(RuteDatabaseService().apply { setTestMode() })


    @Test
    fun h20P1Test(){
        //Given
        UserModel.setMail("testRute@test.test")
        UserModel.setAuthState(AuthState.Authenticated)

        //When
        val result: List<RuteModel>
        runBlocking {
            result = ruteService.getRutes()
        }
        //Then
        assertTrue("Incorrect number of values on the list",result.size == 1)
        assertTrue("Incorrect rute",result.get(0).id == "0064bb23-e362-4554-8c6f-cc3ed92c4fc5")
    }

    @Test(expected = UserNotLoginException::class)
    fun h20P2Test(){
        //Given
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        val result: List<RuteModel>
        runBlocking {
            result = ruteService.getRutes()
        }
        //Then

    }


}
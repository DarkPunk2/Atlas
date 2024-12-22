package com.project.atlas.it_4Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.SessionNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H4UserDeleteTest {
    private var user: UserInterface = AuthService(FireBaseAuthService())

    @Test
    fun h2P1Test() = runBlocking{
        //Given
        val email = "deleteuser@test.test"
        val password = "contraseñaValida@13"
        user.createUser(email,password)
        //When
        val result: Boolean
        result = user.deleteUser()
        //Then
        assertTrue("User is not deleted",result)
        assertEquals("", UserModel.eMail)
        assertEquals(AuthState.Unauthenticated,UserModel.getAuthState())
    }
    @Test(expected= SessionNotFoundException::class)
    fun h2P3Test() {
        //Given
        UserModel.setMail("")
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        runBlocking {
            user.deleteUser()
        }
        //Then

    }
}
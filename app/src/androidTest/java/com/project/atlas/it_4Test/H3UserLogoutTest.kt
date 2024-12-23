package com.project.atlas.it_4Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.SessionNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H3UserLogoutTest {
    private var user: UserInterface = AuthService(FireBaseAuthService())

    @Test
    fun h2P1Test() = runBlocking{
        //Given
        val email = "login@test.test"
        val password = "contraseñaValida@13"
        user.loginUser(email,password)
        //When
        user.logoutUser()
        //Then
        assertEquals("", UserModel.eMail)
        assertEquals(AuthState.Unauthenticated,UserModel.getAuthState())
    }
    @Test(expected= SessionNotFoundException::class)
    fun h2P2Test() {
        //Given
        UserModel.setMail("")
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        user.logoutUser()
        //Then

    }
}
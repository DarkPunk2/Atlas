package com.project.atlas.it_1Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.IncorrectEmailException
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H2UserLoginTest {
    private var user: UserInterface = AuthService(FireBaseAuthService())

    @Test
    fun h2P1Test() = runBlocking{
        //Given

        //When
        val email = "login@test.test"
        val password = "contraseñaValida@13"

        user.loginUser(email,password)
        //Then

        assertEquals(email, UserModel.eMail)
    }
    @Test(expected= UserNotFoundException::class)
    fun h2P2Test() {
        //Given

        //When
        val email = "notlogin@test.test"
        val pass = "Contraseñavalida@13"
        runBlocking {
            user.loginUser(email, pass)
        }
        //Then

    }
    @Test(expected= IncorrectPasswordException::class)
    fun h2P3Test(){
        //Given

        //When
        val email = "login@test.test"
        val password = "12345"
        runBlocking {
            user.loginUser(email, password)
        }
        //Then
    }
    @Test(expected= IncorrectEmailException::class)
    fun h2P4Test(){
        //Given

        //When
        val email = "login@gma"
        val password = "contraseñaValida@13"
        runBlocking {
            user.loginUser(email, password)
        }
        //Then
    }
}
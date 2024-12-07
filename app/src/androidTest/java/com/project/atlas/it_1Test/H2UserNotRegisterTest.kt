package com.project.atlas.it_1Test

import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import kotlinx.coroutines.runBlocking
import org.junit.Test

class H2UserNotRegisterTest {
    private var user: UserInterface = AuthService()

    @Test(expected= UserNotFoundException::class)
    fun h2P2Test() {
        //Given

        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"
        runBlocking {
            user.loginUser(email, pass)
        }
        //Then

    }
}
package com.project.atlas

import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Services.AuthService
import org.junit.Test

class H2UserNotRegisterTest {
    private var user: UserInterface = AuthService()

    @Test(expected= UserNotFoundException::class)
    fun h2P2Test() {
        //Given

        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"

        user.createUser(email, pass)
        //Then

    }
}
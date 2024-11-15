package com.project.atlas

import com.project.atlas.Exceptions.UserAlreadyExistException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.jupiter.api.BeforeEach

class H1UserRegisterTest {

    private lateinit var user: UserInterface

    @BeforeEach
    fun setUp(){
        user = AuthService()
    }
    @AfterEach
    fun

    @Test
    fun acceptationTest_1() {
        //Given
        UserModel.eMail = ""
        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
        //UserInterface

        user.createUser(email, pass)
        //Then
        assertEquals(email,UserModel.eMail)

    }

    @Test(expected=UserAlreadyExistException::class)
    fun acceptationTest_2() {
        //Given
        UserModel.eMail = "usuario@gmail.com"
        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
        user.createUser(email, pass)
        //Then

    }




}
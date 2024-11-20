package com.project.atlas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.BeforeEach

class H2UserLoginTest {
    private lateinit var user: UserInterface
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    @BeforeEach
    fun startup(){
        user = AuthService()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    @Before
    fun userSetup(){
        firebaseAuth.createUserWithEmailAndPassword("usuario@gmail.com","contraseñaValida@13")
        firebaseUser = firebaseAuth.currentUser!!
    }
    @Test
    fun h2P1Test(){
        //Given

        //When
        val email = "usuario@gmail.com"
        val password = "contraseñaValida@13"
        user.loginUser(email,password)
        //Then
        assertEquals(email, UserModel.eMail)
    }
    @Test(expected= IncorrectPasswordException::class)
    fun h2P3Test(){
        //Given

        //When
        val email = "usuario@gmail.com"
        val password = "12345"
        user.loginUser(email,password)
        //Then
    }
    @Test(expected= IncorrectEmailException::class)
    fun h2P4Test(){
        //Given

        //When
        val email = "usuario@gma"
        val password = "contraseñaValida@13"
        user.loginUser(email,password)
        //Then
    }
    @After
    fun deleteUser(){
        firebaseUser.delete()
    }

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
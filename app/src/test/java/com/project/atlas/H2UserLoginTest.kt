package com.project.atlas

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class H2UserLoginTest {
    private lateinit var user: UserInterface
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    @BeforeEach
    fun starup(){
        fun setUp(){
            user = AuthService()
            firebaseAuth = FirebaseAuth.getInstance()
        }
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
    @After
    fun deleteUser(){
        firebaseUser.delete()
    }
}
package com.project.atlas.it_1Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H2UserLoginTest {
    private lateinit var user: UserInterface
    private lateinit var firebaseAuth: FirebaseAuth


    @Before
    fun userSetup() = runBlocking {
        user = AuthService()
        firebaseAuth = FirebaseAuth.getInstance()

        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword("usuario@gmail.com", "contraseñaValida@13").await()
        } catch (e: Exception) {
            // Handle the exception if necessary
            println("Error creating user: ${e.message}")
        }
    }

    @Test
    fun h2P1Test() = runBlocking{
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
        runBlocking {
            user.loginUser(email, password)
        }
        //Then
    }
    @Test(expected= IncorrectEmailException::class)
    fun h2P4Test(){
        //Given

        //When
        val email = "usuario@gma"
        val password = "contraseñaValida@13"
        runBlocking {
            user.loginUser(email, password)
        }
        //Then
    }
    @After
    fun deleteUser(){
        firebaseAuth.currentUser?.delete()
    }
}
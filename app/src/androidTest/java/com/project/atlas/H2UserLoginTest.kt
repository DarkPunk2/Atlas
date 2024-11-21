package com.project.atlas

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H2UserLoginTest {
    private lateinit var user: UserInterface
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser


    @Before
    fun userSetup(){
        user = AuthService()
        firebaseAuth.createUserWithEmailAndPassword("usuario@gmail.com","contraseñaValida@13")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseUser = firebaseAuth.currentUser!!
                }
            }
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

}
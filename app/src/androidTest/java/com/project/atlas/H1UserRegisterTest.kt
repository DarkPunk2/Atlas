package com.project.atlas

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H1UserRegisterTest {

    private lateinit var user: UserInterface
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firebaseUser: FirebaseUser

    @Before
    fun setUp(){
        user = AuthService()
    }

    @Test
    fun acceptationTest_1() {
        //Given

        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
        //UserInterface
        runBlocking {
            user.createUser(email, pass)
        }
        //Then
        assertEquals(email,UserModel.eMail)

    }

    @Test(expected=UserAlreadyExistException::class)
    fun acceptationTest_2() {
        //Given
        //Añade al usuario a Firebase
        user = AuthService()
        firebaseAuth.createUserWithEmailAndPassword("usuario@gmail.com","contraseñaValida@13")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseUser = firebaseAuth.currentUser!!
                }
            }
        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
       runBlocking {
           user.createUser(email, pass)
       }
        //Then
        //Borra el usuario
        firebaseUser.delete()
    }
}
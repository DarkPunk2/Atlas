package com.project.atlas.it_1Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
        user = AuthService(FireBaseAuthService())
    }

    @Test
    fun acceptationTest_1() {
        //Given

        //When
        val email = "create@test.test"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
        //UserInterface
        runBlocking {
            user.createUser(email, pass)
        }
        //Then
        assertEquals(email,UserModel.eMail)
        runBlocking {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                firebaseUser = firebaseAuth.currentUser!!
                firebaseUser.delete()
            }.await()
        }
    }

    @Test(expected=UserAlreadyExistException::class)
    fun acceptationTest_2() {
        //Given

        //When
        val email = "usuario@gmail.com"
        val pass = "Contraseñavalida@13"    //Debe contener una mayuscula,
                                            // una minuscula un special char y un número
       runBlocking {
           user.createUser(email, pass)
       }
        //Then

    }
}
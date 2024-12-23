package com.project.atlas.it_1Test

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.CompletableFuture


class H2UserLoginTest {
    private lateinit var user: UserInterface

    @Test
    fun h2P1Test() = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        val firebaseUser = mock(FirebaseUser::class.java)
        `when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(firebaseUser)

        user = AuthService(firebaseAuth)
        //When
        val email = "login@test.test"
        val password = "contraseñaValida@13"
        runBlocking {
            user.loginUser(email, password)
        }

        //Then

        assertEquals(email, UserModel.eMail)
    }
    @Test(expected= UserNotFoundException::class)
    fun h2P2Test() = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        val firebaseUser = mock(FirebaseUser::class.java)
        `when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenAnswer{
            throw UserNotFoundException("User not found")
        }
        user = AuthService(firebaseAuth)
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
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        user = AuthService(firebaseAuth)
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
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        user = AuthService(firebaseAuth)
        //When
        val email = "login@gma"
        val password = "contraseñaValida@13"
        runBlocking {
            user.loginUser(email, password)
        }
        //Then
    }
}
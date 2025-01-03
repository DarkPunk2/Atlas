package com.project.atlas.it_1Test

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.ArgumentMatchers.anyString

class H1UserRegisterTest {

    private lateinit var user: UserInterface

    @Test
    fun integrationTest_1() = runBlocking {
        // Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        val firebaseUser = mock(FirebaseUser::class.java)
        `when`(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(firebaseUser)

        user = AuthService(firebaseAuth)
        //When
        val email = "login@test.test"
        val password = "contraseñaValida@13"


        runBlocking {
            user.createUser(email, password)
        }

        // Then
        assertEquals(email, UserModel.eMail)
    }

    @Test(expected = UserAlreadyExistException::class)
    fun integrationTest_2() = runBlocking {
        // Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenAnswer {
            throw UserAlreadyExistException("User already exists")
        }

        user = AuthService(firebaseAuth)

        // When
        val email = "usuario@gmail.com"
        val password = "contraseñaValida@13"

        runBlocking {
            user.createUser(email, password)
        }

        // Then
    }
}

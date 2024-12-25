package com.project.atlas.it_4Test


import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H5PasswordRecoverTest {
    private lateinit var user: UserInterface

    @Test
    fun h5P1Test() = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.restorePassword(anyString())).thenReturn(true)
        `when`(firebaseAuth.checkUserExists(anyString())).thenReturn(true)
        user = AuthService(firebaseAuth)

        //When
        val result = user.recoverPassword("login@test.test")
        //Then
        Assert.assertTrue("Email not send to user", result)
    }

    @Test(expected= UserNotFoundException::class)
    fun h5P2Test(): Unit = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.restorePassword(anyString())).thenAnswer{throw UserNotFoundException("User not register")}
        `when`(firebaseAuth.checkUserExists(anyString())).thenReturn(false)
        user = AuthService(firebaseAuth)

        //When
        user.recoverPassword("notlogin@test.test")
        //Then

    }
}
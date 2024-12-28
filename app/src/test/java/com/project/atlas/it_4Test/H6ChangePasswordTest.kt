package com.project.atlas.it_4Test

import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H6ChangePasswordTest {
    private lateinit var user: UserInterface

    @Test
    fun h6P1Test(): Unit = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.changePassword(anyString(),anyString())).thenReturn(true)
        user = AuthService(firebaseAuth)
        //When
        val result = user.changePassword("contraseñaValida@13","NewPassword@13", "NewPassword@13")
        //Then
        assertTrue("Password not changed", result)
    }
    @Test(expected= IncorrectPasswordException::class)
    fun h6P4Test(): Unit = runBlocking {
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.changePassword(anyString(),anyString())).thenReturn(false)
        user = AuthService(firebaseAuth)
        //When
        user.changePassword("contraseñaValida@13","NewPassword@13", "OtherPassword@13")
        //Then

    }
    @Test(expected= IncorrectPasswordException::class)
    fun h6P5Test(): Unit = runBlocking {
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.changePassword(anyString(),anyString())).thenReturn(false)
        user = AuthService(firebaseAuth)
        //When
        user.changePassword("contraseñaValida@13","NewPassword", "NewPassword")
        //Then

    }
}
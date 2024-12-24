package com.project.atlas.it_4Test


import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H5PasswordRecoverTest {
    private lateinit var user: UserInterface

    @Test
    fun h2P1Test() = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.restorePassword()).thenReturn(true)
        user = AuthService(firebaseAuth)

        //When
        val result = user.recoverPassword("login@test.test")
        //Then
        Assert.assertTrue("Email not send to user", result)
    }

    @Test(expected= UserNotFoundException::class)
    fun h2P2Test(): Unit = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.restorePassword()).thenReturn(false)
        user = AuthService(firebaseAuth)


        //When
        user.recoverPassword("notlogin@test.test")
        //Then

    }
}
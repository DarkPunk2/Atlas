package com.project.atlas.it_4Test


import com.project.atlas.exceptions.SessionNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class H3UserLogoutTest {
    private lateinit var user: UserInterface

    @Test
    fun h3P1Test() = runBlocking{
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.logout()).thenReturn(true)
        user = AuthService(firebaseAuth)

        UserModel.setMail("login@test.test")
        UserModel.setAuthState(AuthState.Authenticated)
        //When
        user.logoutUser()
        //Then
        assertEquals("", UserModel.eMail)
        assertEquals(AuthState.Unauthenticated,UserModel.getAuthState())
    }

    @Test(expected= SessionNotFoundException::class)
    fun h3P2Test() {
        //Given
        val firebaseAuth = mock(FireBaseAuthService::class.java)
        `when`(firebaseAuth.logout()).thenReturn(false)
        user = AuthService(firebaseAuth)

        UserModel.setMail("")
        UserModel.setAuthState(AuthState.Unauthenticated)
        //When
        user.logoutUser()
        //Then

    }
}
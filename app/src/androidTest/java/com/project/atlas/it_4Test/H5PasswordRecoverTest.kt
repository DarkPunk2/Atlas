package com.project.atlas.it_4Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H5PasswordRecoverTest {
    private var user: UserInterface = AuthService(FireBaseAuthService())

    @Test
    fun h2P1Test() = runBlocking{
        //Given

        //When
        val result = user.recoverPassword("login@test.test")
        //Then
        assertTrue("Email not send to user", result)
    }
    @Test(expected= UserNotFoundException::class)
    fun h2P2Test() {
        //Given

        //When
        user.recoverPassword("notlogin@test.test")
        //Then

    }
}
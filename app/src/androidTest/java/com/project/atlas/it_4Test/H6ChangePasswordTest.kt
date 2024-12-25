package com.project.atlas.it_4Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class H6ChangePasswordTest {
    private var user: UserInterface = AuthService(FireBaseAuthService())

    @Test
    fun h2P1Test(): Unit = runBlocking{
        //Given
        val email = "changepassword@test.test"
        val password = "contraseñaValida@13"
        user.createUser(email,password)
        //When
        val result = user.changePassword("NewPassword@13", "NewPassword@13")
        //Then
        assertTrue("Password not changed", result)
        user.deleteUser()
    }
    @Test(expected= IncorrectPasswordException::class)
    fun h2P4Test(): Unit = runBlocking {
        //Given

        //When
        user.changePassword("NewPassword@13", "OtherPassword@13")
        //Then

    }
    @Test(expected= IncorrectPasswordException::class)
    fun h2P5Test(): Unit = runBlocking {
        //Given

        //When
        user.changePassword("NewPassword", "NewPassword")
        //Then

    }
}
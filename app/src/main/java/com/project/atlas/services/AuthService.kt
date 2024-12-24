package com.project.atlas.services

import com.project.atlas.exceptions.IncorrectEmailException
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.exceptions.ServiceNotAvailableException
import com.project.atlas.exceptions.SessionNotFoundException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel


class AuthService(externalAuth: FireBaseAuthService) : UserInterface {
    private val auth = externalAuth

    override fun initUser(){
        if (auth.getUser() != null){
            UserModel.setMail(auth.getUser()!!.email!!)
            UserModel.setAuthState(AuthState.Authenticated)
        }
    }

    override suspend fun createUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            throw IncorrectEmailException("Email can't be empty")
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IncorrectEmailException("Email is not valid")
        }
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
        if (!password.matches(regex)) {
            throw IncorrectPasswordException("Password is not valid")
        }

        UserModel.setAuthState(AuthState.Loading)
        try {
            val user = auth.createUserWithEmailAndPassword(email, password)
            if (user != null) {
                UserModel.setMail(email)
                UserModel.setAuthState(AuthState.Authenticated)
            }
        } catch (e: Exception) {
            UserModel.setAuthState(AuthState.Unauthenticated)
            throw e
        }
    }


    override suspend fun loginUser(email: String, password: String) {
        if (email.isEmpty()) {
            throw IncorrectEmailException("Email can't be empty")
        }
        if (password.isEmpty()) {
            throw IncorrectPasswordException("Password can't be empty")
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IncorrectEmailException("Email is not valid")
        }
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
        if (!password.matches(regex)) {
            throw IncorrectPasswordException("Password is not valid")
        }

        UserModel.setAuthState(AuthState.Loading)
        try {
            val user = auth.signInWithEmailAndPassword(email, password)
            if (user != null) {
                UserModel.setMail(email)
                UserModel.setAuthState(AuthState.Authenticated)
            }
        } catch (e: Exception) {
            UserModel.setAuthState(AuthState.Unauthenticated)
            throw e
        }
    }

    override fun logoutUser() {
        if (UserModel.getAuthState() == AuthState.Unauthenticated){
            throw SessionNotFoundException("User is not login")
        }
        if (auth.logout()) {
            UserModel.setAuthState(AuthState.Unauthenticated)
            UserModel.setMail("")
        }else{
            throw ServiceNotAvailableException("Firebase can't logout")
        }

    }

    override suspend fun deleteUser(): Boolean {
        if (UserModel.getAuthState() == AuthState.Unauthenticated){
            throw SessionNotFoundException("User is not login")
        }
        if (auth.deleteUser()) {
            UserModel.setAuthState(AuthState.Unauthenticated)
            UserModel.setMail("")
            return true
        }else{
            return false
        }
    }

    override suspend fun recoverPassword(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IncorrectEmailException("Email is not valid")
        }
        if (!auth.checkUserExists(email)){
            throw UserNotFoundException("User not register in DataBase")
        }
        return auth.restorePassword(email)
    }

}



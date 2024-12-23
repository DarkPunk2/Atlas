package com.project.atlas.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.project.atlas.exceptions.IncorrectEmailException
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthService : UserInterface {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun initUser(){
        if (auth.currentUser != null){
            UserModel.setMail(auth.currentUser!!.email!!)
            UserModel.setAuthState(AuthState.Authenticated)
        }
    }

    override suspend fun createUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            throw IncorrectEmailException("Email can't be empty")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IncorrectEmailException("Email is not valid")
        }
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
        if (!password.matches(regex)) {
            throw IncorrectPasswordException("Password is not valid")
        }
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        UserModel.setMail(email)
                        UserModel.setAuthState(AuthState.Authenticated)
                        continuation.resume(Unit)
                    } else {
                        val exception = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> UserAlreadyExistException("User already exists")
                            else -> Exception(task.exception?.message ?: "Unknown error")
                        }
                        continuation.resumeWithException(exception)
                    }
                }
        }
    }

    override suspend fun loginUser(email: String, password: String) {
        if (email.isEmpty()) {
            throw IncorrectEmailException("Email can't be empty")
        }
        if (password.isEmpty()) {
            throw IncorrectPasswordException("Password can't be empty")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IncorrectEmailException("Email is not valid")
        }
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
        if (!password.matches(regex)) {
            throw IncorrectPasswordException("Password is not valid")
        }

        UserModel.setAuthState(AuthState.Loading)
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        UserModel.setMail(email)
                        UserModel.setAuthState(AuthState.Authenticated)
                        continuation.resume(Unit)
                    } else {
                        UserModel.setAuthState(AuthState.Unauthenticated)
                        continuation.resumeWithException(UserNotFoundException(task.exception?.message ?: "User not found"))
                    }
                }
        }
    }
}



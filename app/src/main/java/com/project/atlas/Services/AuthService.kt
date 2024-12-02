package com.project.atlas.Services

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Exceptions.UserAlreadyExistException
import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.AuthState
import com.project.atlas.Models.UserModel

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthService : UserInterface {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var exception: Exception? = null

    override fun createUser(email: String, password: String) {


        if (email.isEmpty() || password.isEmpty()) {
            throw IncorrectEmailException("Email can't be empty")
        }
        if (password.isEmpty()){
            throw IncorrectPasswordException("Email can't be empty")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            throw IncorrectEmailException("Email is not valid")
        }
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()
        if (!password.matches(regex)){
            throw IncorrectPasswordException("Password is not valid")
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UserModel.setMail(email)
                    UserModel.setAuthState(AuthState.Authenticated)
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException){
                        exception = task.exception
                    }
                }
            }
        if (exception != null){
            throw UserAlreadyExistException("User already exists")
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



package com.project.atlas.Services

import com.google.firebase.auth.FirebaseAuth
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.AuthState
import com.project.atlas.Models.UserModel

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthService : UserInterface {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun createUser(eMail: String, pass: String) {
        // Implementación aquí
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



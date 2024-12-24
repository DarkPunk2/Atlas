package com.project.atlas.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireBaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        createFirestoreDocument(email)
                        continuation.resume(task.result?.user)
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

    private fun createFirestoreDocument(email: String) {
        db.collection("users").document("user@example.com").set(email)
            .addOnFailureListener { e ->
                throw Exception(e.message)
            }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result?.user)
                    } else {
                        UserModel.setAuthState(AuthState.Unauthenticated)
                        continuation.resumeWithException(
                            UserNotFoundException(
                                task.exception?.message ?: "User not found"
                            )
                        )
                    }
                }
        }
    }

    fun logout(): Boolean {
        auth.signOut()
        return auth.currentUser == null
    }

    suspend fun deleteUser(): Boolean {
        val currentUser = auth.currentUser ?: throw UserNotFoundException("No current user found")

        return suspendCoroutine { continuation ->
            currentUser.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.signOut()
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(
                            UserNotFoundException(
                                task.exception?.message ?: "User not found"
                            )
                        )
                    }
                }
        }
    }

    suspend fun restorePassword(email: String): Boolean {
        return suspendCoroutine { continuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(
                            UserNotFoundException(
                                task.exception?.message ?: "User not found"
                            )
                        )
                    }
                }
        }
    }

    suspend fun checkUserExists(email: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        return try {
            val document = db.collection("users").document(email).get().await()
            document.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
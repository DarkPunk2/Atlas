package com.project.atlas.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.exceptions.WrongPasswordException
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import kotlinx.coroutines.runBlocking
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
                        runBlocking {
                            createFirestoreDocument(email.lowercase())
                        }
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

    private suspend fun createFirestoreDocument(email: String) {
        val user = hashMapOf("email" to email)
        db.collection("users").document(email).set(user)
            .addOnFailureListener { e ->
                throw Exception(e.message)
            }.await()
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
                        runBlocking {
                            clearFireStore(UserModel.eMail)
                        }
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

    private suspend fun clearFireStore(email: String) {
        db.collection("users").document(email.lowercase()).delete()
            .addOnFailureListener { e ->
                throw Exception(e.message)
            }.await()
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

    suspend fun changePassword(oldPassword: String,newPassword: String): Boolean {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(UserModel.eMail, oldPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.let {
                            it.updatePassword(newPassword)
                            continuation.resume(true)
                        } ?: run {
                            throw UserNotFoundException("User is not log in")
                        }
                    } else {
                        throw WrongPasswordException("Old password is incorrect")
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
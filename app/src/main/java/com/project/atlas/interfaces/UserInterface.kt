package com.project.atlas.interfaces

interface UserInterface {
    fun initUser()
    suspend fun createUser(email: String, password: String)
    suspend fun loginUser(email: String, password: String)
    fun logoutUser()
    suspend fun deleteUser(): Boolean
    suspend fun recoverPassword(email: String): Boolean
    suspend fun changePassword(oldPassword: String,newPassword: String, confirmPassword: String): Boolean
}
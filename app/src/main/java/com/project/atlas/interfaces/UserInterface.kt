package com.project.atlas.interfaces

interface UserInterface {
    fun initUser()
    suspend fun createUser(email: String, password: String)
    suspend fun loginUser(email: String, password: String)
}
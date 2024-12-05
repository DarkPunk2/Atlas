package com.project.atlas.interfaces

interface UserInterface {

    suspend fun createUser(eMail: String, pass: String)
    suspend fun loginUser(email: String, password: String)

}
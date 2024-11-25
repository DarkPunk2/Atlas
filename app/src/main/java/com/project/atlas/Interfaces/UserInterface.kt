package com.project.atlas.Interfaces

interface UserInterface {

    fun createUser(eMail: String, pass: String)
    fun loginUser(email: String, password: String)

}
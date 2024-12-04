package com.project.atlas.models

object UserModel {
    lateinit var eMail: String
    private var authState: AuthState = AuthState.Unauthenticated

    fun setMail(newMail: String){
        this.eMail = newMail
    }

    fun getAuthState(): AuthState {
        return authState
    }

    fun setAuthState(state: AuthState){
        authState = state
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

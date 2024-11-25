package com.project.atlas.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.AuthState
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService


class UserViewModel: ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authService: UserInterface = AuthService()

    init {
        _authState.value = UserModel.getAuthState()
    }

    fun login(email: String, password: String) {
        //authentication is loading
        _authState.value = AuthState.Loading

        //Attempt to log in the user
        authService.loginUser(email, password)

        //The result of the attempt is assigned
        _authState.value = UserModel.getAuthState()
    }

}
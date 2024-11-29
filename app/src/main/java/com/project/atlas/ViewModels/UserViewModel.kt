package com.project.atlas.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.AuthState
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authService: UserInterface = AuthService()

    init {
        _authState.value = UserModel.getAuthState()
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                authService.loginUser(email, password)
                _authState.value = UserModel.getAuthState()
            } catch (inPass: IncorrectPasswordException) {
                _authState.value = AuthState.Error(inPass.message.toString())
            } catch (inMail: IncorrectEmailException) {
                _authState.value = AuthState.Error(inMail.message.toString())
            } catch (noUser: UserNotFoundException) {
                _authState.value = AuthState.Error(noUser.message.toString())
            }
        }
    }
}

package com.project.atlas.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.atlas.Exceptions.IncorrectEmailException
import com.project.atlas.Exceptions.IncorrectPasswordException
import com.project.atlas.Exceptions.UserAlreadyExistException
import com.project.atlas.Exceptions.UserNotFoundException
import com.project.atlas.Interfaces.UserInterface
import com.project.atlas.Models.AuthState
import com.project.atlas.Models.UserModel
import com.project.atlas.Services.AuthService


class UserViewModel: ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authService: UserInterface

    init {
        _authState.value = UserModel.getAuthState()
        authService = AuthService()
    }

    fun login(email: String, password: String) {
        //authentication is loading
        _authState.value = AuthState.Loading

        //Attempt to log in the user
        try {
            authService.loginUser(email, password)
            _authState.value = UserModel.getAuthState()
        } catch (inPass: IncorrectPasswordException){
            _authState.value = AuthState.Error(inPass.message.toString())
        } catch (inMail: IncorrectEmailException){
            _authState.value = AuthState.Error(inMail.message.toString())
        } catch (noUser: UserNotFoundException){
            _authState.value = AuthState.Error(noUser.message.toString())
        }
    }

    fun createUser(email: String, password: String) {
        //authentication is loading
        _authState.value = AuthState.Loading

        //Attempt to log in the user
        try {
            authService.createUser(email, password)
            _authState.value = UserModel.getAuthState()
        } catch (inPass: IncorrectPasswordException){
            _authState.value = AuthState.Error(inPass.message.toString())
        } catch (inMail: IncorrectEmailException){
            _authState.value = AuthState.Error(inMail.message.toString())
        } catch (noUser: UserNotFoundException){
            _authState.value = AuthState.Error(noUser.message.toString())
        } catch (alreadyUser: UserAlreadyExistException){
            _authState.value = AuthState.Error(alreadyUser.message.toString())
        }
    }


}
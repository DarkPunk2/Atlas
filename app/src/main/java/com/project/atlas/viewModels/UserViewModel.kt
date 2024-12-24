package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.project.atlas.exceptions.IncorrectEmailException
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authService: UserInterface = AuthService(FireBaseAuthService())

    init {
        authService.initUser()
        _authState.value = UserModel.getAuthState()
    }

    fun createUser(email: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                authService.createUser(email, password)
                _authState.value = UserModel.getAuthState()
            } catch (inPass: IncorrectPasswordException) {
                _authState.value = AuthState.Error(inPass.message.toString())
            } catch (inMail: IncorrectEmailException) {
                _authState.value = AuthState.Error(inMail.message.toString())
            } catch (noUser: UserNotFoundException) {
                _authState.value = AuthState.Error(noUser.message.toString())
            } catch (alreadyUser: UserAlreadyExistException){
                _authState.value = AuthState.Error(alreadyUser.message.toString())
            }
        }
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

    fun logout(){
        authService.logoutUser()
        _authState.value = UserModel.getAuthState()
    }

    fun delete(){
        viewModelScope.launch {
            if (authService.deleteUser()){
                _authState.value = UserModel.getAuthState()
            }
        }
    }
}

package com.project.atlas.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.atlas.exceptions.IncorrectEmailException
import com.project.atlas.exceptions.IncorrectPasswordException
import com.project.atlas.exceptions.UserAlreadyExistException
import com.project.atlas.exceptions.UserNotFoundException
import com.project.atlas.exceptions.WrongPasswordException
import com.project.atlas.interfaces.UserInterface
import com.project.atlas.models.AuthState
import com.project.atlas.models.ChangeState
import com.project.atlas.models.UserModel
import com.project.atlas.services.AuthService
import com.project.atlas.services.FireBaseAuthService
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val authService: UserInterface = AuthService(FireBaseAuthService())

    private val _changeState = MutableLiveData<ChangeState>()
    val changeState: LiveData<ChangeState> = _changeState

    init {
        authService.initUser()
        _authState.value = UserModel.getAuthState()
        _changeState.value = ChangeState.NotChange
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

    fun recoverPassword(email: String){
        viewModelScope.launch {
            try {
                if (authService.recoverPassword(email)){
                    _changeState.value = ChangeState.Changed
                }
            }catch (inMail: IncorrectEmailException){
                _changeState.value = ChangeState.Error(inMail.message.toString())
            }catch (userNotFound: UserNotFoundException){
                _changeState.value = ChangeState.Error(userNotFound.message.toString())
            }
        }
    }
    fun changePassword(oldPassword: String,newPassword: String, confirmPassword: String){
        viewModelScope.launch {
            try {
                if (authService.changePassword(oldPassword,newPassword,confirmPassword)){
                    _changeState.value = ChangeState.Changed
                }
            }catch (inPass: IncorrectPasswordException){
                _changeState.value = ChangeState.Error(inPass.message.toString())
            }catch (userNotFound: UserNotFoundException){
                _changeState.value = ChangeState.Error(userNotFound.message.toString())
            }catch (inPass: WrongPasswordException){
                _changeState.value = ChangeState.Error(inPass.message.toString())
            }
        }
    }

    fun goChangePage(){
        _changeState.value = ChangeState.NotChange
    }

}

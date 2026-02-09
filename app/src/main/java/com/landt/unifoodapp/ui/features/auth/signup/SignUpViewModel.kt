package com.landt.unifoodapp.ui.features.auth.signup

import androidx.lifecycle.ViewModel
import com.landt.unifoodapp.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    private val _uniState = MutableStateFlow<SignupEvent>(SignupEvent.Nothing)
    val uniState = _uniState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignupNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _name  = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onEmailChange(email:String) {
        _email.value = email
    }

    fun onPasswordChange(password:String) {
        _password.value = password
    }

    fun onSignUpClick() {
        _uniState.value = SignupEvent.Success
        _uniState.value = SignupEvent.Loading
        _navigationEvent.tryEmit(SignupNavigationEvent.NavigateToHome)
    }

    sealed class SignupNavigationEvent {
        object NavigateToLogin : SignupNavigationEvent()
        object NavigateToHome : SignupNavigationEvent()
    }
    sealed class SignupEvent {
        object Nothing : SignupEvent()
        object Success : SignupEvent()
        object Error : SignupEvent()
        object Loading : SignupEvent()
    }
}
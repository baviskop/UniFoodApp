package com.landt.unifoodapp.ui.features.auth.signin

import androidx.lifecycle.viewModelScope
import com.landt.unifoodapp.data.FoodApi
import com.landt.unifoodapp.data.models.SignInRequest
import com.landt.unifoodapp.ui.features.auth.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(override val foodApi: FoodApi) : BaseAuthViewModel(foodApi) {


    private val _uiState = MutableStateFlow<SigninEvent>(SigninEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SigninNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()


    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onSignInClick() {
        viewModelScope.launch {
            _uiState.value = SigninEvent.Loading
            try {
                val response = foodApi.signIn(
                    SignInRequest(
                        email = email.value,
                        password = password.value)
                )
                if (response.token.isNotEmpty()) {
                    _uiState.value = SigninEvent.Success
                    _navigationEvent.emit(SigninNavigationEvent.NavigateToHome)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SigninEvent.Error
            }
            }
    }

    fun onSignUpClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SigninNavigationEvent.NavigateToSignUp)
        }
    }

    sealed class SigninNavigationEvent {
        object NavigateToSignUp : SigninNavigationEvent()
        object NavigateToHome : SigninNavigationEvent()
    }

    sealed class SigninEvent {
        object Nothing : SigninEvent()
        object Success : SigninEvent()
        object Error : SigninEvent()
        object Loading : SigninEvent()
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value = SigninEvent.Loading
        }
    }

    override fun onGoogleError(msg: String) {
        viewModelScope.launch {
            _uiState.value = SigninEvent.Error
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch {
            _uiState.value = SigninEvent.Error
        }
    }

    override fun onSocialLoginSuccess(token: String) {
        viewModelScope.launch {
            _uiState.value = SigninEvent.Success
            _navigationEvent.emit(SigninNavigationEvent.NavigateToHome)
        }
    }
}
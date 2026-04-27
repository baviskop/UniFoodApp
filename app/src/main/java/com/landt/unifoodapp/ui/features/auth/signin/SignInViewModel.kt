package com.landt.unifoodapp.ui.features.auth.signin

import androidx.lifecycle.viewModelScope
import com.landt.unifoodapp.data.FoodApi
import com.landt.unifoodapp.data.models.SignInRequest
import com.landt.unifoodapp.data.remote.ApiResponse
import com.landt.unifoodapp.data.remote.safeApiCall
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
                val response = safeApiCall {
                    foodApi.signIn(
                        SignInRequest(
                            email = email.value,
                            password = password.value)
                    )
                }
                when(response) {
                    is ApiResponse.Success -> {
                        _uiState.value = SigninEvent.Success
                        _navigationEvent.emit(SigninNavigationEvent.NavigateToHome)
                    }
                    else -> {
                        val err = (response as? ApiResponse.Error)?.code ?: 0
                        error = "Sign In Failed"
                        errorDescription = "Failed to sign up"
                        when(err){
                            400 -> {
                                error = "Invalid Credentials"
                                errorDescription = "Please enter correct details."
                            }
                        }
                        _uiState.value = SigninEvent.Error
                    }
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
            errorDescription = msg
            error = "Google Sign In Failed"
            _navigationEvent.emit(SigninNavigationEvent.NavigateToHome)
            _uiState.value = SigninEvent.Error
        }
    }

    override fun onFacebookError(msg: String) {
        viewModelScope.launch{
            errorDescription = msg
            error = "Facebook Sign In Failed"
            _navigationEvent.emit(SigninNavigationEvent.NavigateToHome)
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
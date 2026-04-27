package com.landt.unifoodapp.ui.features.auth

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.landt.unifoodapp.data.FoodApi
import com.landt.unifoodapp.data.auth.GoogleAuthUIProvider
import com.landt.unifoodapp.data.models.AuthResponse
import com.landt.unifoodapp.data.models.OAuthRequest
import com.landt.unifoodapp.data.remote.ApiResponse
import com.landt.unifoodapp.data.remote.safeApiCall
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
    var error: String =""
    var errorDescription=""
    private val googleAuthUIProvider = GoogleAuthUIProvider()
    private lateinit var callbackManager: CallbackManager

    abstract fun loading()
    abstract fun onGoogleError(msg: String)
    abstract fun onFacebookError(msg: String)
    abstract fun onSocialLoginSuccess(token: String)


    fun onGoogleClicked(context: ComponentActivity){
        initiateGoogleLogin(context)
    }
    fun onFacebookClicked(context: ComponentActivity) {
        initiateFacebookLogin(context)
    }

    protected fun initiateGoogleLogin(context: ComponentActivity) {
        viewModelScope.launch {
            loading()
            try {
                val response = googleAuthUIProvider.signIn(
                    context,
                    CredentialManager.create(context)
                )
                fetchFoodAppToken(response.token, "google") {
                    onGoogleError(it)
                }
            } catch (e: Throwable) {
                onGoogleError(e.message.toString())
            }

           }
    }
    private fun fetchFoodAppToken(token:String, provider: String, onError: (String) -> Unit){
        viewModelScope.launch {
            val request= OAuthRequest(
                token = token,
                provider = provider
            )
            val res = safeApiCall {
                foodApi.oAuth(request)
            }
            when(res){
                is ApiResponse.Success -> {
                    onSocialLoginSuccess(res.data.token)
                }
                else -> {
                    val error = (res as? ApiResponse.Error)?.code
                    if(error != null){
                        when(error){
                            401-> onError("Invalid token")
                            500-> onError("Server error")
                            404-> onError("Not found")
                            else -> onError("Failed")
                        }
                    }
                    else{
                        onError("Failed")
                    }
                }
            }
        }
    }
    protected fun initiateFacebookLogin(context: ComponentActivity) {
        loading()
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    fetchFoodAppToken(result.accessToken.token,"facebook"){
                        onFacebookError(it)
                    }
                }

                override fun onCancel() {
                    onFacebookError("Cancelled")
                }

                override fun onError(exception: FacebookException) {
                    onFacebookError("Failed: ${exception.message}")
                }
            })
        LoginManager.getInstance().logInWithReadPermissions(
            context,
            callbackManager,
            listOf("public_profile", "email")
        )
    }

}
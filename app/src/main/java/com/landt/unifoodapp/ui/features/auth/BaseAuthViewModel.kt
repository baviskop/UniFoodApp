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
import com.landt.unifoodapp.data.models.OAuthRequest
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
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
            val response = googleAuthUIProvider.signIn(
                context,
                CredentialManager.create(context)
            )

            if (response!= null) {
                val request = OAuthRequest(
                    token = response.token,
                    provider = "google"
                )
                val res = foodApi.oAuth(request)
                if (res.token.isNotEmpty()) {
                    onSocialLoginSuccess(res.token)
                }else{
                    onGoogleError("Failed")
                }
            }else {
                onGoogleError("Failed")
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
                    viewModelScope.launch {
                        val request = OAuthRequest(
                            token = result.accessToken.token,
                            provider = "facebook"
                        )
                        val res = foodApi.oAuth(request)
                        if (res.token.isNotEmpty()) {
                            onSocialLoginSuccess(res.token)
                        }else{
                            onFacebookError("Failed not token")
                        }
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
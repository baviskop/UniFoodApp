package com.landt.unifoodapp.ui.features.auth.signin

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.landt.unifoodapp.R
import com.landt.unifoodapp.data.FoodApi
import com.landt.unifoodapp.data.models.AuthResponse
import com.landt.unifoodapp.data.models.SignInRequest
import com.landt.unifoodapp.data.models.SignUpRequest
import com.landt.unifoodapp.ui.GroupSocialButtons
import com.landt.unifoodapp.ui.UniFoodTextField
import com.landt.unifoodapp.ui.navigation.AuthScreen
import com.landt.unifoodapp.ui.navigation.Home
import com.landt.unifoodapp.ui.navigation.Login
import com.landt.unifoodapp.ui.navigation.SignUp
import com.landt.unifoodapp.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val email = viewModel.email.collectAsStateWithLifecycle()
        val password = viewModel.password.collectAsStateWithLifecycle()
        val errorMessage = remember { mutableStateOf<String?>(null) }
        val loading = remember { mutableStateOf(false) }

        val uiState = viewModel.uiState.collectAsState()
        when (uiState.value) {
            is SignInViewModel.SigninEvent.Error -> {
                loading.value = false
                errorMessage.value = "Failed"
            }

            is SignInViewModel.SigninEvent.Loading -> {
                loading.value = true
                errorMessage.value = null
            }

            else -> {
                loading.value = false
                errorMessage.value = null
            }
        }
        val context = LocalContext.current
        LaunchedEffect(true) {
            viewModel.navigationEvent.collectLatest { event ->
                when (event) {
                    is SignInViewModel.SigninNavigationEvent.NavigateToHome -> {
                        navController.navigate(Home) {
                            popUpTo(AuthScreen) {
                                inclusive = true
                            }
                        }
                    }

                    is SignInViewModel.SigninNavigationEvent.NavigateToSignUp -> {
                        navController.navigate(SignUp)
                    }
                }
            }
        }


        Image(
            painter = painterResource(id = R.drawable.ic_auth_bg), contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.sign_in),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(40.dp))
//            Spacer(modifier = Modifier.padding(16.dp))
            UniFoodTextField(
                value = email.value,
                onValueChange = { viewModel.onEmailChange(it) },
                label = {
                    Text(text = stringResource(id = R.string.email), color = Color.DarkGray)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(16.dp))
            UniFoodTextField(
                value = password.value,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = {
                    Text(text = stringResource(id = R.string.password), color = Color.DarkGray)
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(text = errorMessage.value ?: "", color = Color.Red)
            Button(
                onClick = viewModel::onSignInClick,
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(32.dp),

                ) {
                Box {
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith fadeOut(
                                tween(300)
                            ) + scaleOut(targetScale = 0.8f)
                        }) { target ->
                        if (target) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .size(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.sign_in),
//                                modifier = Modifier.padding(horizontal = 32.dp)
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = stringResource(id = R.string.dont_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onSignInClicked()
                    }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(16.dp))
            GroupSocialButtons(color = Color.Black, onFacebookClick = {})
            { }
        }
    }
}

@Composable
fun SignInRoute(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    SignInScreen(
        navController = navController,
        viewModel = viewModel)
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    SignInScreen(
        rememberNavController(),
        viewModel = SignInViewModel(object : FoodApi {
            override suspend fun getFood(): List<String> = emptyList()
            override suspend fun signUp(request: SignUpRequest): AuthResponse =
                AuthResponse("token")
            override suspend fun signIn(request: SignInRequest): AuthResponse =
                AuthResponse("token")
        })
    )
}

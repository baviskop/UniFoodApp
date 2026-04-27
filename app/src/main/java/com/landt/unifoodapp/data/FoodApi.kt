package com.landt.unifoodapp.data

import com.landt.unifoodapp.data.models.AuthResponse
import com.landt.unifoodapp.data.models.CategoriesResponse
import com.landt.unifoodapp.data.models.OAuthRequest
import com.landt.unifoodapp.data.models.SignInRequest
import com.landt.unifoodapp.data.models.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    ///Request
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/signin")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>
    ///Response

}

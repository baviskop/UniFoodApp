package com.landt.unifoodapp.data

import com.landt.unifoodapp.data.models.AuthResponse
import com.landt.unifoodapp.data.models.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/food")
    suspend fun getFood(): List<String>

    ///Request
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    ///Response

}

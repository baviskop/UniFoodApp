package com.landt.unifoodapp.data.models


data class UpdateCartItemRequest(
    val cartItemId: String,
    val quantity: Int
)

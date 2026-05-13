package com.landt.unifoodapp.data.models

data class ConfirmPaymentRequest(
    val paymentIntentId: String,
    val addressId: String
)

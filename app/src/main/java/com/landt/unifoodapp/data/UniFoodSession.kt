package com.landt.unifoodapp.data

import android.content.Context
import android.content.SharedPreferences

class UniFoodSession(context: Context) {
    val sharedPres: SharedPreferences = context.getSharedPreferences("UniFoodSession", Context.MODE_PRIVATE)

    fun storeToken(token: String) {
        sharedPres.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        sharedPres.getString("token", null)?.let{
            return it
        }
        return null
    }
}
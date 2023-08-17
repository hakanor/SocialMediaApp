package com.example.socialmediaapp.service

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("AWSLogin", Context.MODE_PRIVATE)

    fun userLoggedIn(accessToken:String, refreshToken:String, username:String) {
        sharedPrefs.edit().putString("accessToken",accessToken).apply()
        sharedPrefs.edit().putString("refreshToken",refreshToken).apply()
        sharedPrefs.edit().putString("username",username).apply()
    }

    fun userSignOut() {
        sharedPrefs.edit().remove("accessToken").apply()
        sharedPrefs.edit().remove("refreshToken").apply()
        sharedPrefs.edit().remove("username").apply()
    }

    fun getCurrentUser(): String? {
        var username = sharedPrefs.getString("username","")
        return username
    }

    fun getCurrentAccessToken(): String? {
        var token  = sharedPrefs.getString("accessToken","")
        return token
    }
    fun getCurrentRefreshToken(): String? {
        var token  = sharedPrefs.getString("refreshToken","")
        return token
    }

    fun updateAccessToken(accessToken: String) {
        sharedPrefs.edit().remove("accessToken").apply()
    }
}

package com.example.socialmediaapp.service

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("AWSLogin", Context.MODE_PRIVATE)
    private val awsToken = "userLoggedIn"

    fun userLoggedIn(token:String,username:String) {
        sharedPrefs.edit().putString("token",token).apply()
        sharedPrefs.edit().putString("username",username).apply()
    }

    fun userSignOut() {
        sharedPrefs.edit().remove("token").apply()
        sharedPrefs.edit().remove("username").apply()
    }

    fun getCurrentUser(): String? {
        var username = sharedPrefs.getString("username","")
        return username
    }

    fun getCurrentToken(): String? {
        var token  = sharedPrefs.getString("token","")
        return token
    }
}

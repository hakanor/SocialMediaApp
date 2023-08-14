package com.example.socialmediaapp.service

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesService(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("AWSLogin", Context.MODE_PRIVATE)
    private val userLoggedInKey = "userLoggedIn"

    fun userLoggedIn() {
        sharedPrefs.edit().putBoolean(userLoggedInKey, true).apply()
    }

    fun userSignOut() {
        sharedPrefs.edit().putBoolean(userLoggedInKey, false).apply()
    }

    fun getLogInState(): Boolean {
        return sharedPrefs.getBoolean(userLoggedInKey, false)
    }
}

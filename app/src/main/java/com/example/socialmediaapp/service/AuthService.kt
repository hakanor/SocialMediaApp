package com.example.socialmediaapp.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants
import org.json.JSONObject

enum class AuthServiceActions(val value:String) {
    login("login"),
    register("register"),
    changePassword("changePassword"),
    resetPassword("resetPassword"),
    confirmForgotPassword("confirmForgotPassword"),
    mfa("mfa"),
    getUser("getUser"),
    validateAccessToken("validateAccessToken")
}

interface AuthServiceCallback {
    fun onLogin(message: String)
    fun onLogOut(message: String)
    fun onRegister(message: String)
    fun onError(error: String)
}

class AuthService (private val appContext: Context, private var callback: AuthServiceCallback){
    fun login(username: String, password: String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.login.value, // Use enum value here
            "username" to username,
            "password" to password
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    if (jsonResponse.has("accessToken")) {
                        val token = jsonResponse.getString("accessToken")
                        val sharedPreferencesService = SharedPreferencesService(appContext)
                        sharedPreferencesService.userLoggedIn(token,username)
                        callback.onLogin("Login success")
                    } else {
                        callback.onError(responseBody.toString())
                    }
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
}
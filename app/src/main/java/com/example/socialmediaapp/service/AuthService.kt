package com.example.socialmediaapp.service

import android.content.Context
import android.util.Log
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants
import org.json.JSONObject

enum class AuthServiceActions(val value:String) {
    Login("login"),
    Register("register"),
    ChangePassword("changePassword"),
    ResetPassword("resetPassword"),
    ConfirmForgotPassword("confirmForgotPassword"),
    MFA("mfa"),
    GetUser("getUser"),
    Logout("logout"),
    ValidateAccessToken("validateAccessToken")
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
            "action" to AuthServiceActions.Login.value, // Use enum value here
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
    fun logOut() {
        val apiService = ApiService()
        val spService = SharedPreferencesService(appContext)
        val token = spService.getCurrentToken()
        val username = spService.getCurrentUser()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.Logout.value, // Use enum value here
            "token" to token,
            "username" to username,
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    spService.userSignOut()
                    callback.onLogOut(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
    fun registerUser(username: String, password: String,name: String, surname: String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.Register.value, // Use enum value here
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
                    val dbService = UserDBService()
                    dbService.createNewUser(name,surname)
                    callback.onRegister(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
}
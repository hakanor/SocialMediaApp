package com.example.socialmediaapp.service

import android.content.Context
import android.util.Log
import com.example.socialmediaapp.Constants
import org.json.JSONObject

enum class AuthServiceActions(val value:String) {
    Login("login"),
    Register("register"),
    ChangePassword("changePassword"),
    ResetPassword("resetPassword"),
    ConfirmForgotPassword("confirmForgotPassword"),
    RespondToMfaChallenge("respondToMfaChallenge"),
    Logout("logout"),
}

interface AuthServiceCallback {
    fun onLogin(message: String)
    fun onLoginChallenge(message:String)
    fun onLogOut(message: String)
    fun onRegister(message: String)
    fun onError(error: String)
    fun onSuccess(message:String)
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
                    if (jsonResponse.has("challengeSession")) {
                        val challengeSession = jsonResponse.getString("challengeSession")
                        callback.onLoginChallenge(challengeSession)
                    } else {
                        callback.onError(responseBody.toString())
                    }
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }

    fun respondToMfaChallenge(username:String, challengeSession: String, mfaCode: String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.RespondToMfaChallenge.value, // Use enum value here
            "challengeSession" to challengeSession,
            "mfaCode" to mfaCode,
            "username" to username
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    if (jsonResponse.has("accessToken")) {
                        val accessToken = jsonResponse.getString("accessToken")
                        val refreshToken = jsonResponse.getString("refreshToken")
                        val sharedPreferencesService = SharedPreferencesService(appContext)
                        sharedPreferencesService.userLoggedIn(accessToken,refreshToken,username)
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
        val token = spService.getCurrentAccessToken()
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
    fun changePassword(password: String, newPassword: String) {
        val apiService = ApiService()
        val spService = SharedPreferencesService(appContext)
        val token = spService.getCurrentAccessToken()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.ChangePassword.value,
            "token" to token,
            "password" to password,
            "newPassword" to newPassword
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                Log.d("auth",responseBody.toString())
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    callback.onSuccess(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
    fun sendResetPasswordMail(email: String) {
        val apiService = ApiService()
        val spService = SharedPreferencesService(appContext)
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.ResetPassword.value,
            "username" to email
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                Log.d("auth",responseBody.toString())
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    callback.onSuccess(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }

    fun resetPassword(code: String, username:String, password: String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to AuthServiceActions.ConfirmForgotPassword.value,
            "code" to code,
            "username" to username,
            "newPassword" to password
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                Log.d("auth",responseBody.toString())
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    callback.onSuccess(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
}
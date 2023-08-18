package com.example.socialmediaapp.service

import android.content.Context
import com.example.socialmediaapp.Constants
import org.json.JSONObject

enum class TokenServiceActions(val value:String) {
    GetUser("getUser"),
    RefreshAccessToken("refreshAccessToken"),
    ValidateAccessToken("validateAccessToken")
}

interface TokenServiceCallback {
    fun onValidAccessToken(message: String) {
        throw UnsupportedOperationException("onValidAccessToken must be overridden")
    }
    fun onRefreshAccessToken(message: String) {
        throw UnsupportedOperationException("onRefreshAccessToken must be overridden")
    }
    fun onGetUser(message: String) {
        throw UnsupportedOperationException("onGetUser must be overridden")
    }
    fun onError(error: String) {
        throw UnsupportedOperationException("onError must be overridden")
    }
    fun onSuccess(message:String) {
        throw UnsupportedOperationException("onSuccess must be overridden")
    }
}

class TokenService (private val appContext: Context, private var callback: TokenServiceCallback){

    fun validateAccessToken(token:String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to TokenServiceActions.ValidateAccessToken.value,
            "token" to token
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    callback.onValidAccessToken(jsonResponse.toString())
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }

    fun refreshAccessToken(username:String, refreshToken:String) {
        val apiService = ApiService()
        val url = Constants.BASE_URL + "/auth"
        val method = "POST"
        val jsonBody = mapOf(
            "action" to TokenServiceActions.RefreshAccessToken.value,
            "username" to username,
            "refreshToken" to refreshToken
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
                        callback.onRefreshAccessToken(accessToken.toString())
                    } else {
                        callback.onError(responseBody.toString())
                    }
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }

    fun getUserSubId(username:String) {
        val apiService = ApiService()
        val url = Constants.URL_AUTH
        val method = "POST"
        val jsonBody = mapOf(
            "action" to TokenServiceActions.GetUser.value,
            "username" to username
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode, error ->
            if (error != null) {
                callback.onError(error.message.toString())
            } else {
                if (responseCode == 200) {
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val userAttributes = jsonResponse.getJSONArray("UserAttributes")

                    if (userAttributes.length() > 0) {
                        val firstAttribute = userAttributes.getJSONObject(0)
                        val subId = firstAttribute.getString("Value")
                        callback.onGetUser(subId)
                    } else {
                        callback.onError("No User or ID found.")
                    }
                } else {
                    callback.onError(responseBody.toString())
                }
            }
        }
    }
}
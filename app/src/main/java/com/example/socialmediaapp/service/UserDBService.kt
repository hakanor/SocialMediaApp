package com.example.socialmediaapp.service

import android.util.Log
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants

class UserDBService {
    private val apiService = ApiService()
    private val url = Constants.URL_USERS

    fun createNewUser (name: String, surname: String) {
        val method = "POST"
        val jsonBody = mapOf(
            "name" to name,
            "surname" to surname,
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)
        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode,error ->
            if (error != null) {
                error.printStackTrace()
            } else {
                responseBody?.let {
                    Log.d("Register", "Register successful.")
                }
            }
        }
    }
}
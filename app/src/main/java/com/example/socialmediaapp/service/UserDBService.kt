package com.example.socialmediaapp.service

import android.util.Log
import com.example.socialmediaapp.Constants

class UserDBService {
    private val apiService = ApiService()
    private val url = Constants.URL_USERS

    fun createNewUser (userId:String, nameSurname: String, phoneNumber: String) {
        val method = "POST"
        val jsonBody = mapOf(
            "userId" to userId,
            "nameSurname" to nameSurname,
            "phoneNumber" to phoneNumber
        )
        val requestBody = apiService.createJsonStringFromMap(jsonBody)
        Log.d("Register",requestBody.toString())
        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode,error ->
            if (error != null) {
                error.printStackTrace()
            } else {
                responseBody?.let {
                    Log.d("Register", responseBody.toString())
                    Log.d("Register", responseCode.toString())
                }
            }
        }
    }
}
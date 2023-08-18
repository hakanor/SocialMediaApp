package com.example.socialmediaapp.service

import android.content.Context
import com.example.socialmediaapp.model.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ApiService (private val accessToken: String? = null) {
    private val apiKey = "txSCMdHUWaajE22Z7uwFr4L5w15Hifyk8SOewyOR"
    private val client = OkHttpClient()
    fun sendHttpRequestWithApiKey(url: String, method: String, requestBody: String?, callback: (String?, Int?, Exception?) -> Unit) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBody?.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", apiKey)
            .addHeader("Authorization", accessToken?:"")
            .method(method, body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val responseCode = response.code
                callback(responseBody, responseCode, null)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, 0,e)
            }
        })
    }
    fun parseJsonToPost(json: String): Post {
        val gson = Gson()
        return gson.fromJson(json, Post::class.java)
    }
    fun parseJsonToPostList(json: String): List<Post> {
        val gson = Gson()
        val listType = object : TypeToken<List<Post>>() {}.type
        return gson.fromJson(json, listType)
    }
    fun createJsonStringFromMap(jsonBody: Map<String, String?>): String? {
        return jsonBody?.let { JSONObject(it).toString() }
    }
}
package com.example.myapplication.service

import com.example.socialmediaapp.model.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiService {
    private val apiKey = "txSCMdHUWaajE22Z7uwFr4L5w15Hifyk8SOewyOR"
    private val client = OkHttpClient()

    fun sendHttpRequestWithApiKey(url: String, method: String, requestBody: String?, callback: (String?, Exception?) -> Unit) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBody?.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", apiKey)
            .method(method, body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                callback(responseBody, null)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
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
}


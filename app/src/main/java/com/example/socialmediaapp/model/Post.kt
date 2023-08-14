package com.example.socialmediaapp.model
import java.io.Serializable

data class Post (
    val id: String,
    val date: String,
    val userId: String,
    val content: String
) : Serializable

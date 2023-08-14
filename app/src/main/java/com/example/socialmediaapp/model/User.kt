package com.example.socialmediaapp.model
import java.io.Serializable

data class User (
    val userId: String,
    val name: String,
    val surname: String,
    val imageUrl: String
) : Serializable

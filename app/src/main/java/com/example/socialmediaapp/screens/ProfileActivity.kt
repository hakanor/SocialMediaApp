package com.example.socialmediaapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.SharedPreferencesService

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tw = findViewById<TextView>(R.id.textView)

        val sp = SharedPreferencesService(this)
        val uid = sp.getCurrentUser()

        tw.text = uid
    }
}
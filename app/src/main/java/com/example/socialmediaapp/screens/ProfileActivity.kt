package com.example.socialmediaapp.screens


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.socialmediaapp.service.CognitoService
import com.example.socialmediaapp.service.CognitoServiceCallback
import com.example.socialmediaapp.R

class ProfileActivity : AppCompatActivity(), CognitoServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var tw = findViewById<TextView>(R.id.textView)

        var cognito = CognitoService(this,this)

        var uid = cognito.userPool.currentUser.userId

        tw.text = uid
    }

    override fun onLoginSuccess() {
        TODO("Not yet implemented")
    }

    override fun onSignOut() {
        TODO("Not yet implemented")
    }

    override fun onRegisterSuccess() {
        TODO("Not yet implemented")
    }
}
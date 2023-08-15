package com.example.socialmediaapp.screens

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.AuthService
import com.example.socialmediaapp.service.AuthServiceCallback
import com.example.socialmediaapp.service.SharedPreferencesService

import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity(), AuthServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val userNameTextEdit = findViewById<TextInputEditText>(R.id.userNameEditText)
        val passwordTextEdit = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)

        checkUserLoggedIn()

        loginButton.setOnClickListener {
            val username = userNameTextEdit.text.toString()
            val password = passwordTextEdit.text.toString()

            if(TextUtils.isEmpty(userNameTextEdit.text) || TextUtils.isEmpty(passwordTextEdit.text)){
                Toast.makeText(this@LoginActivity,"Please fill out all fields.",Toast.LENGTH_SHORT).show()
            } else {
                val authService = AuthService(this,this)
                authService.login(username,password)
            }
        }

        signUpText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkUserLoggedIn () {
        val sp = SharedPreferencesService(this)
        Log.d("logout",sp.getCurrentToken().toString())
        if (sp.getCurrentUser() != "" && sp.getCurrentToken() != "") {
            navigateToHomeActivity()
        } else {
            Log.d("LoginActivity", "User is not logged in.")
        }
    }
    private fun navigateToHomeActivity () {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onLogin(message: String) {
        runOnUiThread{
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
            navigateToHomeActivity()
        }
    }

    override fun onLogOut(message: String) {
        runOnUiThread{
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        }
    }

    override fun onRegister(message: String) {
        TODO("Not yet implemented")
    }

    override fun onError(error: String) {
        runOnUiThread{
            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        }
    }
    override fun onSuccess(message: String) {
        TODO("Not yet implemented")
    }
}
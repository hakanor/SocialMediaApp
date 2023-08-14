package com.example.socialmediaapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.CognitoService
import com.example.socialmediaapp.service.CognitoServiceCallback
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity(), CognitoServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        var passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        var passwordEditText2 = findViewById<TextInputEditText>(R.id.passwordEditText2)
        var signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val cognitoService = CognitoService(this,this)

            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(TextUtils.isEmpty(emailEditText.text) || TextUtils.isEmpty(passwordEditText.text)){
                Toast.makeText(this@RegisterActivity,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.text.toString() != passwordEditText2.text.toString()) {
                Toast.makeText(this@RegisterActivity,"Passwords doesn't match.", Toast.LENGTH_SHORT).show()
            } else {
                cognitoService.signUpInBackground(username, password)
            }
        }
    }
    override fun onLoginSuccess() {
    }

    override fun onSignOut() {
    }

    override fun onRegisterSuccess() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
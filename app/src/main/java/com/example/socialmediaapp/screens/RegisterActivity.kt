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
        var nameEditText = findViewById<TextInputEditText>(R.id.nameEditText)
        var surnameEditText = findViewById<TextInputEditText>(R.id.surnameEditText)
        var passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        var signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val cognitoService = CognitoService(this,this)

            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()

            if(TextUtils.isEmpty(emailEditText.text)
                || TextUtils.isEmpty(passwordEditText.text)
                || TextUtils.isEmpty(nameEditText.text)
                || TextUtils.isEmpty(surnameEditText.text)){
                Toast.makeText(this@RegisterActivity,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }  else {
                cognitoService.signUpInBackground(username, password, name, surname)
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
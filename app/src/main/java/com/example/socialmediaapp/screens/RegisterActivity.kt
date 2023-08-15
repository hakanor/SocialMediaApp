package com.example.socialmediaapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.AuthService
import com.example.socialmediaapp.service.AuthServiceCallback
import com.example.socialmediaapp.service.UserDBService
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity(), AuthServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        var nameEditText = findViewById<TextInputEditText>(R.id.nameEditText)
        var surnameEditText = findViewById<TextInputEditText>(R.id.surnameEditText)
        var passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        var signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val authService = AuthService(this,this)

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
                authService.registerUser(username,password,name,surname)
            }
        }
    }
    override fun onLogin(message: String) {
        TODO("Not yet implemented")
    }

    override fun onLogOut(message: String) {
        TODO("Not yet implemented")
    }

    override fun onRegister(message: String) {
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onError(error: String) {
        TODO("Not yet implemented")
    }
}
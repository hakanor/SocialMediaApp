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
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity(), AuthServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val nameSurnameEditText = findViewById<TextInputEditText>(R.id.nameSurnameEditText)
        val phoneNumberEditText = findViewById<TextInputEditText>(R.id.phoneNumberEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val authService = AuthService(this,this)

            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val nameSurname = nameSurnameEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            if(TextUtils.isEmpty(emailEditText.text)
                || TextUtils.isEmpty(passwordEditText.text)
                || TextUtils.isEmpty(nameSurnameEditText.text)
                || TextUtils.isEmpty(phoneNumberEditText.text)){
                Toast.makeText(this@RegisterActivity,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }  else {
                authService.registerUser(username,password,nameSurname,phoneNumber)
            }
        }
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
        runOnUiThread {
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        }
    }
}
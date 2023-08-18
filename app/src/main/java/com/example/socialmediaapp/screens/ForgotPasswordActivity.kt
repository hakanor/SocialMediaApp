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

class ForgotPasswordActivity : AppCompatActivity(), AuthServiceCallback {
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailResetEditText)
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)

        resetPasswordButton.setOnClickListener {
            val authService = AuthService(this,this)

            email = emailEditText.text.toString()

            if(TextUtils.isEmpty(emailEditText.text)) {
                Toast.makeText(this,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            } else {
                authService.sendResetPasswordMail(email)
            }


        }
    }

    override fun onLogin(message: String) {
        TODO("Not yet implemented")
    }

    override fun onLoginChallenge(message: String) {
        TODO("Not yet implemented")
    }

    override fun onLogOut(message: String) {
        TODO("Not yet implemented")
    }

    override fun onRegister(message: String) {
        TODO("Not yet implemented")
    }

    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(message: String) {
        runOnUiThread {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfirmCodeActivity::class.java)
            intent.putExtra("username",email)
            startActivity(intent)
        }
    }
}
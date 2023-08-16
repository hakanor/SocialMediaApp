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

class ResetPasswordActivity : AppCompatActivity(), AuthServiceCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = intent.getStringExtra("code") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        setContentView(R.layout.activity_reset_password)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val password2EditText = findViewById<TextInputEditText>(R.id.password2EditText)
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)

        resetPasswordButton.setOnClickListener {
            val authService = AuthService(this,this)

            val newPassword = passwordEditText.text.toString()
            val newPassword2 = password2EditText.text.toString()

            if( TextUtils.isEmpty(passwordEditText.text) || TextUtils.isEmpty(password2EditText.text)){
                Toast.makeText(this,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }  else if (newPassword2 != newPassword) {
                Toast.makeText(this,"Passwords doesn't match.", Toast.LENGTH_SHORT).show()
            }
            else {
                authService.resetPassword(code,username,newPassword)
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
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
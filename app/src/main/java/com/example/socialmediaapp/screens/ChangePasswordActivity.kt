package com.example.socialmediaapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.AuthService
import com.example.socialmediaapp.service.AuthServiceCallback
import com.google.android.material.textfield.TextInputEditText

class ChangePasswordActivity : AppCompatActivity(), AuthServiceCallback{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        val oldPasswordEditText = findViewById<TextInputEditText>(R.id.oldPasswordEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val password2EditText = findViewById<TextInputEditText>(R.id.password2EditText)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val authService = AuthService(this,this)

            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val newPassword2 = password2EditText.text.toString()

            if(TextUtils.isEmpty(oldPasswordEditText.text)
                || TextUtils.isEmpty(passwordEditText.text)
                || TextUtils.isEmpty(password2EditText.text)){
                Toast.makeText(this,"Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }  else if (newPassword2 != newPassword) {
                Toast.makeText(this,"Passwords doesn't match.", Toast.LENGTH_SHORT).show()
            }
            else {
                authService.changePassword(oldPassword,newPassword)
            }
        }
    }
    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(message: String) {
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
    }
}
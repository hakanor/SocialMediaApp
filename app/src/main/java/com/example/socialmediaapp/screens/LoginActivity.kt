package com.example.socialmediaapp.screens

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.AuthService
import com.example.socialmediaapp.service.AuthServiceCallback
import com.example.socialmediaapp.service.SharedPreferencesService
import com.example.socialmediaapp.service.TokenService
import com.example.socialmediaapp.service.TokenServiceCallback

import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity(), AuthServiceCallback, TokenServiceCallback {

    private lateinit var authService: AuthService
    private lateinit var tokenService: TokenService
    private lateinit var sharedPreferencesService : SharedPreferencesService
    private lateinit var loadingProgressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val userNameTextEdit = findViewById<TextInputEditText>(R.id.userNameEditText)
        val passwordTextEdit = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpText = findViewById<TextView>(R.id.signUpText)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)

        authService = AuthService(this,this)
        tokenService = TokenService(this,this)
        sharedPreferencesService = SharedPreferencesService(this)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE
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

        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkUserLoggedIn () {
        val sp = SharedPreferencesService(this)
        val currentAccessToken =  sp.getCurrentAccessToken()
        val currentUsername =  sp.getCurrentUser()
        if (currentUsername != null && currentAccessToken != null) {
            tokenService.validateAccessToken(currentAccessToken)
        } else {
            Log.d("debug", "User is not logged in.")
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
    override fun onValidAccessToken(message: String) {
        if (message.contains("true")) {
            navigateToHomeActivity()
        } else {
            runOnUiThread {
                Toast.makeText(this,"AccesToken expired, generating new accessToken.",Toast.LENGTH_SHORT).show()
                sharedPreferencesService.userRemoveAccessToken()
                var user = sharedPreferencesService.getCurrentUser()
                tokenService.getUserSubId(user?:"")
            }
        }
    }

    override fun onRefreshAccesToken(message: String) {
        sharedPreferencesService.updateAccessToken(message)
        navigateToHomeActivity()
    }

    override fun onGetUser(message: String) {
        val refreshToken = sharedPreferencesService.getCurrentRefreshToken()
        if (refreshToken != null ){
            tokenService.refreshAccessToken(message,refreshToken)
        }
    }
    override fun onError(error: String) {
        runOnUiThread{
            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        }
    }
    override fun onSuccess(message: String) {

    }
}
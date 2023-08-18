package com.example.socialmediaapp.screens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var username : String
    private lateinit var challengeSession : String

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
        checkUserLoggedIn()

        loginButton.setOnClickListener {
            username = userNameTextEdit.text.toString()
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

    fun showAlertDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_alert_layout, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).show()

        val alertLogin = mDialogView.findViewById<Button>(R.id.alertLogin)
        val editText1 = mDialogView.findViewById<EditText>(R.id.editTextAlert1)
        val editText2 = mDialogView.findViewById<EditText>(R.id.editTextAlert2)
        val editText3 = mDialogView.findViewById<EditText>(R.id.editTextAlert3)
        val editText4 = mDialogView.findViewById<EditText>(R.id.editTextAlert4)
        val editText5 = mDialogView.findViewById<EditText>(R.id.editTextAlert5)
        val editText6 = mDialogView.findViewById<EditText>(R.id.editTextAlert6)

        val editTexts = mutableListOf<EditText>(
            editText1, editText2, editText3, editText4, editText5, editText6
        )

        for (i in 0 until editTexts.size - 1) {
            val currentEditText = editTexts[i]
            val nextEditText = editTexts[i + 1]

            currentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        nextEditText.requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        if (alertLogin != null) {
            alertLogin.setOnClickListener {
                val editTexts = arrayOf(
                    editText1, editText2, editText3, editText4, editText5, editText6
                )

                val anyEditTextIsNull = editTexts.any { it?.text.isNullOrEmpty() }

                if (anyEditTextIsNull) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    val code = editTexts.joinToString(separator = "") { it?.text.toString() }
                    authService.respondToMfaChallenge(username, challengeSession, code)
                }
            }
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

    override fun onLoginChallenge(message: String) {
        challengeSession = message
        runOnUiThread {
            showAlertDialog()
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
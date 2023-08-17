package com.example.socialmediaapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.socialmediaapp.service.ApiService
import com.example.socialmediaapp.Constants
import com.example.socialmediaapp.R
import com.example.socialmediaapp.service.CognitoService
import com.example.socialmediaapp.service.CognitoServiceCallback
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date

class CreatePostActivity : AppCompatActivity(), CognitoServiceCallback{

    private lateinit var dateTextEdit : TextInputEditText
    private lateinit var contentTextEdit : TextInputEditText
    private lateinit var userId : String
    private lateinit var content : String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        dateTextEdit = findViewById(R.id.dateTextEdit)
        contentTextEdit = findViewById(R.id.contentTextEdit)
        var createPostButton = findViewById<Button>(R.id.createPostButton)

        setCurrentDateAndTime()

        var cognito = CognitoService(this,this)
        this.userId = cognito.getCurrentUserId()

        createPostButton.setOnClickListener {
            content = contentTextEdit.text.toString()
            createNewPost(userId,content,date)
        }
    }

    private fun createNewPost (userId: String, content: String, date:String) {
        val requestBody = "{\"userId\":\"$userId\", \"content\": \"$content\", \"date\": \"$date\" }"
        val apiService = ApiService()
        val url = Constants.URL_POSTS
        val method = "POST"

        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode,error ->
            if (error != null) {
                runOnUiThread{
                    Toast.makeText(this@CreatePostActivity, error.toString(), Toast.LENGTH_SHORT).show()
                }
            } else {
                responseBody?.let {
                    runOnUiThread{
                        Toast.makeText(this@CreatePostActivity, responseBody, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun setCurrentDateAndTime() {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        date = currentDate
        dateTextEdit.setText(currentDate)
    }

    override fun onLoginSuccess() {
        TODO("Not yet implemented")
    }

    override fun onSignOut() {
        TODO("Not yet implemented")
    }

    override fun onRegisterSuccess() {
        TODO("Not yet implemented")
    }
}
package com.example.socialmediaapp.screens

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import android.app.TimePickerDialog
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants
import com.example.socialmediaapp.R
import com.example.socialmediaapp.model.Post
import java.text.SimpleDateFormat
import java.util.Locale

class EditPostActivity : AppCompatActivity() {

    private lateinit var dateInputEditText : TextInputEditText
    private lateinit var userIdInputEditText : TextInputEditText
    private lateinit var contentEditText : TextInputEditText
    private lateinit var postIdTextView : TextView
    private lateinit var post : Post
    private lateinit var id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        dateInputEditText = findViewById(R.id.editPostDateTextEdit)
        userIdInputEditText = findViewById(R.id.editPostUserIdTextEdit)
        contentEditText = findViewById(R.id.editPostDescriptionTextEdit)
        postIdTextView = findViewById(R.id.editPostIdText)
        val editPostButton = findViewById<Button>(R.id.editPostButton)

        setIntentData()

        dateInputEditText.setOnClickListener {
            openDatePicker()
        }

        editPostButton.setOnClickListener {
            var userId = userIdInputEditText.text
            var content = contentEditText.text
            var date = dateInputEditText.text

            val requestBody = "{\"id\":\"$id\",\"userId\":\"$userId\", \"content\": \"$content\", \"date\": \"$date\"}"
            val apiService = ApiService()
            val url = Constants.URL_POSTS
            val method = "PUT"
            apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, error ->
                if (error != null) {
                    runOnUiThread{
                        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    responseBody?.let {
                        runOnUiThread{
                            Toast.makeText(this, responseBody, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setIntentData() {
        post = intent.getSerializableExtra("EXTRA_POST") as Post
        id = post.id
        postIdTextView.text = "Post ID: $id"
        dateInputEditText.setText(post.date)
        contentEditText.setText(post.content)
        userIdInputEditText.setText(post.userId)
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                        calendar.set(selectedYear, selectedMonth, selectedDayOfMonth, selectedHour, selectedMinute)
                        val selectedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(calendar.time)
                        dateInputEditText.setText(selectedDate)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }
}
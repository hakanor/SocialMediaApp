package com.example.socialmediaapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.socialmediaapp.R

class ConfirmCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_code)
        val username = intent.getStringExtra("username") ?: ""

        val continueButton = findViewById<Button>(R.id.continueResetButton)

        val editText1 = findViewById<EditText>(R.id.editText1)
        val editText2 = findViewById<EditText>(R.id.editText2)
        val editText3 = findViewById<EditText>(R.id.editText3)
        val editText4 = findViewById<EditText>(R.id.editText4)
        val editText5 = findViewById<EditText>(R.id.editText5)
        val editText6 = findViewById<EditText>(R.id.editText6)

        val editTexts = mutableListOf<EditText>(
            findViewById(R.id.editText1),
            findViewById(R.id.editText2),
            findViewById(R.id.editText3),
            findViewById(R.id.editText4),
            findViewById(R.id.editText5),
            findViewById(R.id.editText6)
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

        continueButton.setOnClickListener {
            val editTexts = arrayOf(
                editText1, editText2, editText3, editText4, editText5, editText6
            )

            val anyEditTextIsNull = editTexts.any { it.text.isNullOrEmpty() }

            if (anyEditTextIsNull) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val code = editTexts.joinToString(separator = "") { it.text.toString() }
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("code", code)
                intent.putExtra("username",username)
                startActivity(intent)
            }
        }

    }
}
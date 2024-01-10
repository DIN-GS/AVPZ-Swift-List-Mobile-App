package com.example.avpzmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class RegisterActivity: ComponentActivity() {

    lateinit var fullnameField: EditText
    lateinit var emailField: EditText
    lateinit var firstFieldPassword: EditText
    lateinit var secondFieldPassword: EditText
    lateinit var register: Button
    lateinit var signin: Button

    val webService = WebService()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fullnameField = findViewById(R.id.fullnameField)
        emailField = findViewById(R.id.emailField)
        firstFieldPassword = findViewById(R.id.firstFieldPassword)
        secondFieldPassword = findViewById(R.id.secondFieldPassword)
        register = findViewById(R.id.register)

        register.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val registerSuccess = register(fullnameField, emailField, firstFieldPassword, secondFieldPassword)

                if (registerSuccess) {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        signin = findViewById(R.id.signin)

        signin.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private suspend fun register(fullnameField: EditText, emailField: EditText, firstFieldPassword: EditText, secondFieldPassword: EditText): Boolean{
        val link = "https://swift-list-api.onrender.com/api/auth/register"
        val mediaType = "application/json".toMediaTypeOrNull()
        val fullnameField = fullnameField.text.toString()
        val email = emailField.text.toString()
        val firstPassword = firstFieldPassword.text.toString()
        val secondPassword = secondFieldPassword.text.toString()
        if (firstPassword != secondPassword) return false;
        val body = RequestBody.create(
            mediaType, """
            {
                "fullName": "$fullnameField",
                "email": "$email",
                "password": "$firstPassword"
            }
        """.trimIndent()
        )

        val methodType = "POST"


        return withContext(Dispatchers.IO) {
            webService.makeHttpCall(applicationContext, link, body, "", methodType)
        }
    }
}
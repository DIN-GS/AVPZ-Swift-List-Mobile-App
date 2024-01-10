package com.example.avpzmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
//import com.android.volley.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody

class MainActivity : ComponentActivity() {

    lateinit var passwordEditText: EditText
    lateinit var loginButton: Button
    lateinit var emailEditText: EditText
    lateinit var createAnAccount: Button
    val webService = WebService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_main)

        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        emailEditText = findViewById(R.id.email)

        loginButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val loginSuccess = login(emailEditText, passwordEditText)

                if (loginSuccess) {
                    val intent = Intent(this@MainActivity, ProjectsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        createAnAccount= findViewById(R.id.createAnAccount)

        createAnAccount.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun login(emailEditText: EditText, passwordEditText: EditText): Boolean {
        val link = "https://swift-list-api.onrender.com/api/auth/login"
        val mediaType = "application/json".toMediaTypeOrNull()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val body = RequestBody.create(
            mediaType, """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()
        )
        val methodType = "POST"

        return withContext(Dispatchers.IO) {
            val result = webService.makeHttpCall(applicationContext, link, body, "", methodType)
            result
        }
    }


}

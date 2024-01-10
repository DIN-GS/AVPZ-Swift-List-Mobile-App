package com.example.avpzmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class UsersActivity: ComponentActivity() {

    lateinit var usersList: ListView
    lateinit var list: ArrayList<User>
    lateinit var projectsIconOff: ImageView
    lateinit var tasksIconOff: ImageView
    lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_users)

        val webService = WebService()
        var token = webService.getTokenFromSharedPreferences(this)
        userName = findViewById(R.id.userName)
        userName.text = webService.extractUsernameFromToken(token)
        usersList = findViewById(R.id.usersList)

        GlobalScope.launch(Dispatchers.Main) {
            val usersResponse = getUsers()

            if (usersResponse.success) {
                // Update the UI with the retrieved projectsList
                list = usersResponse.usersList as ArrayList<User>
                updateUI()
            } else {
                // Handle error case
                // You may display an error message or take appropriate action
            }
        }
        projectsIconOff = findViewById(R.id.projectsIconOff)

        projectsIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@UsersActivity, ProjectsActivity::class.java)
                startActivity(intent)
            }
        }

        tasksIconOff = findViewById(R.id.tasksIconOff)

        tasksIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@UsersActivity, TasksActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private suspend fun getUsers(): ResponseForUsers {
        val webService = WebService()
        val token = webService.getTokenFromSharedPreferences(this)
        val link = "https://swift-list-api.onrender.com/api/users"
        val mediaType = "application/json".toMediaTypeOrNull()

        val methodType = "GET"

        return withContext(Dispatchers.IO) {
            webService.makeHttpCallUsers(applicationContext, link, token, methodType)
        }
    }

    private fun updateUI() {
        val adapter = CustomAdapterUsers(this, list)
        usersList.adapter = adapter
    }
}
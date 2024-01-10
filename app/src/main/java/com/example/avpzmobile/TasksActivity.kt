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

class TasksActivity: ComponentActivity() {

    lateinit var tasksList: ListView
    lateinit var list: ArrayList<Task>
    lateinit var projectsIconOff: ImageView
    lateinit var profileIconOff: ImageView
    lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        val webService = WebService()
        var token = webService.getTokenFromSharedPreferences(this)
        userName = findViewById(R.id.userName)
        userName.text = webService.extractUsernameFromToken(token)
        tasksList = findViewById(R.id.tasksList)

        GlobalScope.launch(Dispatchers.Main) {
            val tasksResponse = getTasks()

            if (tasksResponse.success) {
                // Update the UI with the retrieved projectsList
                list = tasksResponse.tasksList as ArrayList<Task>
                updateUI()
            } else {
                // Handle error case
                // You may display an error message or take appropriate action
            }
        }
        projectsIconOff = findViewById(R.id.projectsIconOff)

        projectsIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@TasksActivity, ProjectsActivity::class.java)
                startActivity(intent)
            }
        }

        profileIconOff = findViewById(R.id.profileIconOff)

        profileIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@TasksActivity, UsersActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun getTasks(): ResponseForTasks {
        val webService = WebService()
        val token = webService.getTokenFromSharedPreferences(this)
        val link = "https://swift-list-api.onrender.com/api/tasks"
        val mediaType = "application/json".toMediaTypeOrNull()

        val methodType = "GET"

        return withContext(Dispatchers.IO) {
            webService.makeHttpCallTasks(applicationContext, link, token, methodType)
        }
    }

    private fun updateUI() {
       val adapter = CustomAdapterTasks(this, list)
        tasksList.adapter = adapter
    }
}
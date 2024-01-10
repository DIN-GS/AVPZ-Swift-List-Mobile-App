package com.example.avpzmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import io.jsonwebtoken.Jwt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ProjectsActivity: ComponentActivity()  {

    lateinit var projectsList: ListView
    lateinit var list: ArrayList<Project>
    lateinit var tasksIconOff: ImageView
    lateinit var profileIconOff: ImageView
    lateinit var userName: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        val webService = WebService()
        var token = webService.getTokenFromSharedPreferences(this)
        userName = findViewById(R.id.userName)
        userName.text = webService.extractUsernameFromToken(token)

        projectsList = findViewById(R.id.projectsList)
        GlobalScope.launch(Dispatchers.Main) {
            val projectsResponse = getProjects()

            if (projectsResponse.success) {
                // Update the UI with the retrieved projectsList
                list = projectsResponse.projectsList as ArrayList<Project>
                updateUI()
            } else {
                // Handle error case
                // You may display an error message or take appropriate action
            }
        }

        tasksIconOff= findViewById(R.id.tasksIconOff)

        tasksIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@ProjectsActivity, TasksActivity::class.java)
                startActivity(intent)
            }
        }

        profileIconOff = findViewById(R.id.profileIconOff)

        profileIconOff.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val intent = Intent(this@ProjectsActivity, UsersActivity::class.java)
                startActivity(intent)
            }
        }


    }

    private suspend fun getProjects(): ResponseForProjects {
        val webService = WebService()
        val token = webService.getTokenFromSharedPreferences(this)
        val link = "https://swift-list-api.onrender.com/api/projects/get"
        val mediaType = "application/json".toMediaTypeOrNull()

        val methodType = "GET"

        return withContext(Dispatchers.IO) {
            webService.makeHttpCallProjects(applicationContext, link, token, methodType)
        }
    }

    /*private fun updateUI() {
        // You can update your UI based on the populated projectsList
        // For example, you may use an ArrayAdapter to display the list in a ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        projectsList.adapter = adapter
    }*/

    private fun updateUI() {
        val adapter = CustomAdapterProjects(this, list)
        projectsList.adapter = adapter
    }


}
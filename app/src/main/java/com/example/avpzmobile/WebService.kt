package com.example.avpzmobile

import android.content.Context
import android.content.SharedPreferences
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import android.util.Base64
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT


class WebService {



    suspend fun makeHttpCall(context: Context, link: String, body: RequestBody, token: String, methodType: String): Boolean {
        var success = false
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(link)
            .method(methodType, body)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val token = extractTokenFromResponse(responseBody)
            saveTokenToSharedPreferences(context, token)
            success = true
        }

        response.close()
        return success
    }


    suspend fun makeHttpCallProjects(context: Context, link: String, token: String, methodType: String): ResponseForProjects {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(link)
            .header("Authorization", "Bearer $token") // Add the token to the request header
            .method(methodType, null)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val projectsList = parseProjectsList(responseBody)
                saveTokenToSharedPreferences(context, token)
                return@withContext ResponseForProjects(success = true, projectsList = projectsList)
            } else {
                return@withContext ResponseForProjects(success = false, projectsList = null)
            }
        }
    }

    suspend fun makeHttpCallTasks(context: Context, link: String, token: String, methodType: String): ResponseForTasks {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(link)
            .header("Authorization", "Bearer $token") // Add the token to the request header
            .method(methodType, null)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val tasksList = parseTasksList(responseBody) // Changed parsing function
                saveTokenToSharedPreferences(context, token)
                return@withContext ResponseForTasks(success = true, tasksList = tasksList) // Changed response type
            } else {
                return@withContext ResponseForTasks(success = false, tasksList = null) // Changed response type
            }
        }
    }

    suspend fun makeHttpCallUsers(context: Context, link: String, token: String, methodType: String): ResponseForUsers {
        val client = OkHttpClient.Builder().build()

        val request = Request.Builder()
            .url(link)
            .header("Authorization", "Bearer $token") // Add the token to the request header
            .method(methodType, null)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val usersList = parseUsersList(responseBody) // Changed parsing function
                saveTokenToSharedPreferences(context, token)
                return@withContext ResponseForUsers(success = true, usersList = usersList) // Changed response type
            } else {
                return@withContext ResponseForUsers(success = false, usersList = null) // Changed response type
            }
        }
    }

    private fun parseProjectsList(responseBody: String?): List<Project>? {
        return try {
            val jsonArray = JSONArray(responseBody)
            val projectsList = mutableListOf<Project>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val project = Project(
                    title = jsonObject.getString("title"),
                    description = jsonObject.getString("description")
                )
                projectsList.add(project)
            }

            projectsList
        } catch (e: JSONException) {
            null
        }
    }

    private fun parseTasksList(responseBody: String?): List<Task>? {
        return try {
            val jsonArray = JSONArray(responseBody)
            val tasksList = mutableListOf<Task>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val task = Task(
                    title = jsonObject.getString("title"),
                    description = jsonObject.getString("description")
                )
                tasksList.add(task)
            }

            tasksList
        } catch (e: JSONException) {
            null
        }
    }

    private fun parseUsersList(responseBody: String?): List<User>? {
        return try {
            val jsonArray = JSONArray(responseBody)
            val usersList = mutableListOf<User>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val user = User(
                    fullName = jsonObject.getString("fullname"), // Assuming JSON key for full name
                    email = jsonObject.getString("email")
                )
                usersList.add(user)
            }

            usersList
        } catch (e: JSONException) {
            null
        }
    }

    // Function to extract the token from the response body
    private fun extractTokenFromResponse(responseBody: String?): String {
        // Implement your logic to extract the token from the response body
        // For example, if the token is a JSON field, you can use a JSON parser
        // This is just a placeholder and may need adjustment based on your actual response format
        return responseBody ?: ""
    }

    // Function to save the token to SharedPreferences
    private fun saveTokenToSharedPreferences(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getTokenFromSharedPreferences(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token: String = sharedPreferences.getString("token", "") ?: ""
        return token.removeSurrounding("\"")
    }

    /*fun extractUsernameFromToken(token: String, secretKey: String): String {
        val signingKey = Keys.hmacShaKeyFor(Base64.encode(secretKey.toByteArray(), Base64.DEFAULT))

        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject
    }*/

    fun extractUsernameFromToken(token: String): String {

        try {
            val decodedToken: DecodedJWT = decodeJwt(token)
            // Use JwtRegisteredClaimNames.Name to extract the full name claim
            return decodedToken.getClaim("name").asString() ?: "Unknown"
        } catch (e: Exception) {
            // Handle the exception (e.g., log it or return a default value)
            return "Unknown"
        }
    }

    fun decodeJwt(token: String): DecodedJWT {
        return JWT.decode(token)
    }
}
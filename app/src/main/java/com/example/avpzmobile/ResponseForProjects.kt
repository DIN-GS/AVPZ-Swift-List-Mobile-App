package com.example.avpzmobile

data class ResponseForProjects(
    val success: Boolean,
    val projectsList: List<Project>?
)
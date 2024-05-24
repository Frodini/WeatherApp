package com.example.weatherapplication.beans
data class ChatMessage(
    val user: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val usersPresent: List<String> = listOf()
)

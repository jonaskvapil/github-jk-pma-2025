package com.example.a014sharedtasklist

data class Task(
    val id: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

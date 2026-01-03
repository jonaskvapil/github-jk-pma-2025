package com.example.a013edugame

import androidx.room.*

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
)

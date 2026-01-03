package com.example.a013edugame

import androidx.room.*

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playerName: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

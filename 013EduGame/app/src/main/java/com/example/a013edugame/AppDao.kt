package com.example.a013edugame

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Otázky
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestions(count: Int): List<Question>

    @Insert
    suspend fun insertQuestion(question: Question)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    // Výsledky
    @Query("SELECT * FROM game_results ORDER BY timestamp DESC LIMIT 10")
    fun getRecentResults(): Flow<List<GameResult>>

    @Insert
    suspend fun insertResult(result: GameResult)
}

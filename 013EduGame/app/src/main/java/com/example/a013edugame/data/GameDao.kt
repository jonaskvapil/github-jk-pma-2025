package com.example.a013edugame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // Metoda pro zápis výsledku
    // 'suspend' znamená, že poběží na pozadí a nebude sekat aplikaci
    @Insert
    suspend fun insertResult(result: GameResult)

    // Metoda pro získání žebříčku
    // Vrací Flow - to znamená, že když se databáze změní, UI se automaticky aktualizuje
    @Query("SELECT * FROM game_results ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<GameResult>>
}

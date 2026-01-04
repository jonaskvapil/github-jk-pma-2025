package com.example.a013edugame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity definuje, že toto je tabulka v databázi
@Entity(tableName = "game_results")
data class GameResult(
    // Každý záznam musí mít unikátní ID. autoGenerate = true znamená, že se čísluje samo (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Identita hráče (povinné dle zadání)
    val playerName: String,

    // Dosažené skóre
    val score: Int,

    // Datum, kdy hru dohrál (volitelné, ale dobré pro řazení)
    val timestamp: Long = System.currentTimeMillis()
)

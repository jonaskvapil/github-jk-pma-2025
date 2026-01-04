package com.example.a013edugame.ux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a013edugame.data.GameDao
import com.example.a013edugame.data.GameResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val dao: GameDao) : ViewModel() {

    // 1. Identita hráče (uložíme si ji sem, když ji uživatel zadá)
    var currentPlayerName: String = ""

    // 2. Skóre aktuální hry
    var currentScore: Int = 0

    // 3. Načítání žebříčku (automaticky se aktualizuje)
    // stateIn převede databázový tok na stav, který může UI snadno číst
    val leaderboard = dao.getTopScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. Funkce pro uložení výsledku po hře
    fun saveGameResult() {
        if (currentPlayerName.isNotBlank()) {
            viewModelScope.launch {
                val result = GameResult(playerName = currentPlayerName, score = currentScore)
                dao.insertResult(result)
            }
        }
    }
}

// TOTO JE "FACTORY" - boilerplate kód, který je nutný pro vytvoření instance ViewModelu s parametrem
class GameViewModelFactory(private val dao: GameDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

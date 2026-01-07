package com.example.a013edugame

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.a013edugame.data.AppDatabase
import com.example.a013edugame.data.GameResult
import com.example.a013edugame.ux.GameViewModel
import com.example.a013edugame.ux.GameViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var layoutLogin: LinearLayout
    private lateinit var layoutGame: LinearLayout
    private lateinit var layoutResult: LinearLayout

    private lateinit var etPlayerName: EditText
    private lateinit var etAnswer: EditText
    private lateinit var tvQuestion: TextView
    private lateinit var tvLeaderboard: TextView
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var viewModel: GameViewModel
    private var currentCorrectAnswer: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getDatabase(this)
        val factory = GameViewModelFactory(db.gameDao())
        viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        initViews()

        // --- OPRAVA SMYČKY ZDE ---
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_game -> {
                    if (viewModel.currentPlayerName.isBlank()) {
                        // Voláme showScreen s updateNav = false, aby se nespustil listener znovu
                        showScreen(layoutLogin, updateNav = false)
                    } else {
                        showScreen(layoutGame, updateNav = false)
                    }
                    true
                }
                R.id.nav_leaderboard -> {
                    showScreen(layoutResult, updateNav = false)
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.leaderboard.collect { list ->
                    updateLeaderboardUI(list)
                }
            }
        }

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val name = etPlayerName.text.toString()
            if (name.isNotBlank()) {
                viewModel.currentPlayerName = name
                viewModel.currentScore = 0
                startGame()
            } else {
                Toast.makeText(this, "Vyplň jméno!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnSubmitAnswer).setOnClickListener {
            checkAnswer()
        }


        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            viewModel.currentScore = 0
            viewModel.currentPlayerName = "" // PŘIDEJ TENTO ŘÁDEK (vymaže jméno)
            showScreen(layoutLogin)
            etPlayerName.text.clear()
        }

    }

    private fun initViews() {
        layoutLogin = findViewById(R.id.layoutLogin)
        layoutGame = findViewById(R.id.layoutGame)
        layoutResult = findViewById(R.id.layoutResult)
        etPlayerName = findViewById(R.id.etPlayerName)
        etAnswer = findViewById(R.id.etAnswer)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvLeaderboard = findViewById(R.id.tvLeaderboard)
        bottomNav = findViewById(R.id.bottomNavigation)
    }

    private fun startGame() {
        showScreen(layoutGame)
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        val a = Random.nextInt(1, 10)
        val b = Random.nextInt(1, 10)
        currentCorrectAnswer = a + b
        tvQuestion.text = "$a + $b"
        etAnswer.text.clear()
    }

    private fun checkAnswer() {
        val userAnswer = etAnswer.text.toString().toIntOrNull()

        if (userAnswer == currentCorrectAnswer) {
            viewModel.currentScore++
            Toast.makeText(this, "Správně! +1 bod", Toast.LENGTH_SHORT).show()
            generateNewQuestion()
        } else {
            Toast.makeText(this, "Špatně! Konec hry.", Toast.LENGTH_LONG).show()
            viewModel.saveGameResult()
            showScreen(layoutResult) // Tady se ikona přepne sama díky showScreen
        }
    }

    private fun updateLeaderboardUI(list: List<GameResult>) {
        if (list.isEmpty()) {
            tvLeaderboard.text = "Zatím žádné výsledky.\nBuď první!"
        } else {
            val sb = StringBuilder()
            list.forEachIndexed { index, result ->
                sb.append("${index + 1}. ${result.playerName} — ${result.score} b.\n\n")
            }
            tvLeaderboard.text = sb.toString()
        }
    }

    // --- OPRAVA SMYČKY ZDE ---
    // Přidán parametr updateNav (defaultně true)
    private fun showScreen(viewToShow: View, updateNav: Boolean = true) {
        layoutLogin.visibility = View.GONE
        layoutGame.visibility = View.GONE
        layoutResult.visibility = View.GONE

        viewToShow.visibility = View.VISIBLE

        // Ikonu aktualizujeme JENOM tehdy, když to není voláno z menu
        if (updateNav) {
            if (viewToShow == layoutResult) {
                if (bottomNav.selectedItemId != R.id.nav_leaderboard) {
                    bottomNav.selectedItemId = R.id.nav_leaderboard
                }
            } else {
                if (bottomNav.selectedItemId != R.id.nav_game) {
                    bottomNav.selectedItemId = R.id.nav_game
                }
            }
        }
    }
}

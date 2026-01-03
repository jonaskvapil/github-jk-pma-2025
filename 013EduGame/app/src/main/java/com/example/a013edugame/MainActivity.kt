package com.example.a013edugame

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    private var questions = listOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0
    private var playerName = "Hráč"

    private lateinit var textQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var textScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)
        dao = database.appDao()

        textQuestion = findViewById(R.id.textQuestion)
        radioGroup = findViewById(R.id.radioGroup)
        btnNext = findViewById(R.id.btnNext)
        textScore = findViewById(R.id.textScore)

        btnNext.setOnClickListener { checkAnswer() }

        startGame()
    }

    private fun startGame() {
        lifecycleScope.launch {
            questions = dao.getRandomQuestions(5)
            if (questions.isEmpty()) {
                Toast.makeText(this@MainActivity, "Žádné otázky v DB!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            currentQuestionIndex = 0
            score = 0
            showQuestion()
        }
    }

    private fun showQuestion() {
        if (currentQuestionIndex >= questions.size) {
            endGame()
            return
        }

        val question = questions[currentQuestionIndex]
        textQuestion.text = question.text

        (radioGroup.getChildAt(0) as RadioButton).text = question.option1
        (radioGroup.getChildAt(1) as RadioButton).text = question.option2
        (radioGroup.getChildAt(2) as RadioButton).text = question.option3
        (radioGroup.getChildAt(3) as RadioButton).text = question.option4

        radioGroup.clearCheck()
        textScore.text = "Skóre: $score / ${questions.size}"
    }

    private fun checkAnswer() {
        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Vyber odpověď!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedButton = findViewById<RadioButton>(selectedId)
        val question = questions[currentQuestionIndex]

        if (selectedButton.text == question.correctAnswer) {
            score++
            Toast.makeText(this, "✓ Správně!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "✗ Správně: ${question.correctAnswer}", Toast.LENGTH_LONG).show()
        }

        currentQuestionIndex++
        showQuestion()
    }

    private fun endGame() {
        lifecycleScope.launch {
            dao.insertResult(GameResult(playerName = playerName, score = score, totalQuestions = questions.size))
        }

        textQuestion.text = "Konec hry!\nSkóre: $score / ${questions.size}"
        radioGroup.visibility = RadioGroup.GONE
        btnNext.text = "Nová hra"
        btnNext.setOnClickListener {
            radioGroup.visibility = RadioGroup.VISIBLE
            btnNext.text = "Další"
            startGame()
        }
    }
}

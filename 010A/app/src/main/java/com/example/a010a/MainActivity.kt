package com.example.a010a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var inputField: EditText
    private lateinit var checkButton: Button

    private var randomNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Najdi view podle ID
        titleText = findViewById(R.id.titleText)
        inputField = findViewById(R.id.inputField)
        checkButton = findViewById(R.id.checkButton)

        // Vygeneruj náhodné číslo mezi 1-10
        randomNumber = Random.nextInt(1, 11)

        // Nastav onClick listener pro tlačítko
        checkButton.setOnClickListener {
            checkGuess()
        }
    }

    private fun checkGuess() {
        // Získej text z EditText
        val userInput = inputField.text.toString()

        // Zkontroluj, jestli uživatel něco zadal
        if (userInput.isEmpty()) {
            Toast.makeText(this, "Zadej číslo!", Toast.LENGTH_SHORT).show()
            return
        }

        // Převeď text na číslo
        val guess = userInput.toIntOrNull()

        if (guess == null) {
            Toast.makeText(this, "Zadej platné číslo!", Toast.LENGTH_SHORT).show()
            return
        }

        // Zkontroluj rozsah
        if (guess !in 1..10) {
            Toast.makeText(this, "Číslo musí být mezi 1 a 10!", Toast.LENGTH_SHORT).show()
            return
        }

        // Porovnej s náhodným číslem
        when {
            guess < randomNumber -> {
                Toast.makeText(this, "Vyšší!", Toast.LENGTH_LONG).show()
            }
            guess > randomNumber -> {
                Toast.makeText(this, "Nižší!", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Správně! Číslo bylo $randomNumber", Toast.LENGTH_LONG).show()
                // Vygeneruj nové číslo pro další hru
                randomNumber = Random.nextInt(1, 11)
                inputField.text.clear()
            }
        }
    }
}

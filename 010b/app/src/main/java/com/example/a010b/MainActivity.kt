package com.example.a010b

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.a010b.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializuj ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastav onClick listener pomocí binding
        binding.decideButton.setOnClickListener {
            makeDecision()
        }
    }

    private fun makeDecision() {
        // Získej text pomocí binding
        val input = binding.optionsInput.text.toString().trim()

        if (input.isEmpty()) {
            Toast.makeText(this, "Zadej alespoň jednu možnost!", Toast.LENGTH_SHORT).show()
            binding.resultText.text = ""
            return
        }

        val options = input.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (options.size < 2) {
            Toast.makeText(this, "Zadej alespoň 2 možnosti oddělené čárkou!", Toast.LENGTH_SHORT).show()
            binding.resultText.text = ""
            return
        }

        // Vyber náhodnou možnost
        val randomChoice = options[Random.nextInt(options.size)]

        // Zobraz výsledek pomocí binding
        binding.resultText.text = "🎯 $randomChoice"
        Toast.makeText(this, "Rozhodnuto!", Toast.LENGTH_SHORT).show()
    }
}

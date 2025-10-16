package com.example.a08

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a08.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val prefsName = "user_prefs"
    private val KEY_NAME = "name"
    private val KEY_AGE = "age"
    private val KEY_ADULT = "adult"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val ageText = binding.etAge.text?.toString()?.trim().orEmpty()
            val age = ageText.toIntOrNull()

            if (name.isEmpty() || age == null) {
                Toast.makeText(this, "Vyplň jméno a věk (číslo).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sp.edit()
                .putString(KEY_NAME, name)
                .putInt(KEY_AGE, age)
                .putBoolean(KEY_ADULT, binding.cbAdult.isChecked)
                .apply()

            Toast.makeText(this, "Uloženo.", Toast.LENGTH_SHORT).show()
        }

        binding.btnLoad.setOnClickListener {
            val name = sp.getString(KEY_NAME, "")
            val age = sp.getInt(KEY_AGE, 0)
            val adult = sp.getBoolean(KEY_ADULT, false)

            binding.etName.setText(name)
            binding.etAge.setText(if (age == 0) "" else age.toString())
            binding.cbAdult.isChecked = adult

            Toast.makeText(this, "Načteno.", Toast.LENGTH_SHORT).show()
        }
    }
}
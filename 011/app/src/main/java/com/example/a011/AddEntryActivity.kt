package com.example.a011

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.a011.databinding.ActivityAddEntryBinding
import com.google.android.material.snackbar.Snackbar

class AddEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher pro výběr obrázku
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivSelectedImage.setImageURI(it)

            // Custom Toast s ikonou (pomocí Snackbar)
            Snackbar.make(binding.root, "✓ Obrázek načten", Snackbar.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Nebyl vybrán žádný obrázek", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavení Action Bar s návratem
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Přidat záznam"

        setupListeners()
    }

    private fun setupListeners() {
        // Tlačítko pro výběr obrázku
        binding.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // RadioGroup listener
        binding.rgMood.setOnCheckedChangeListener { _, checkedId ->
            val mood = when (checkedId) {
                binding.rbHappy.id -> "Šťastný"
                binding.rbNeutral.id -> "Neutrální"
                binding.rbSad.id -> "Smutný"
                else -> "Neznámý"
            }
            Toast.makeText(this, "Nálada: $mood", Toast.LENGTH_SHORT).show()
        }

        // Uložení záznamu
        binding.btnSave.setOnClickListener {
            saveEntry()
        }

        // Zrušení
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveEntry() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        // Validace
        if (title.isEmpty()) {
            Toast.makeText(this, "Zadejte název!", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Zadejte popis!", Toast.LENGTH_SHORT).show()
            return
        }

        // Zjištění nálady
        val mood = when (binding.rgMood.checkedRadioButtonId) {
            binding.rbHappy.id -> "Šťastný"
            binding.rbNeutral.id -> "Neutrální"
            binding.rbSad.id -> "Smutný"
            else -> "Neutrální"
        }

        val isImportant = binding.cbImportant.isChecked

        // Uložení do SharedPreferences
        val prefs = getSharedPreferences("diary_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val currentCount = prefs.getInt("entries_count", 0)
        editor.putInt("entries_count", currentCount + 1)
        editor.putString("last_entry_title", title)
        editor.putString("last_entry_description", description)
        editor.putString("last_entry_mood", mood)
        editor.putBoolean("last_entry_important", isImportant)

        selectedImageUri?.let {
            editor.putString("last_entry_image", it.toString())
        }

        editor.apply()

        // Snackbar s akcí
        Snackbar.make(binding.root, "Záznam uložen! ✓", Snackbar.LENGTH_LONG)
            .setAction("ZPĚT") {
                // UNDO pattern - obnovení předchozího stavu
                editor.putInt("entries_count", currentCount)
                editor.apply()
                Toast.makeText(this, "Uložení zrušeno", Toast.LENGTH_SHORT).show()
            }
            .show()

        // Počkat 1.5s a zavřít aktivitu
        binding.root.postDelayed({
            finish()
        }, 1500)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

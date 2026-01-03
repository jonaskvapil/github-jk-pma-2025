package com.example.a012mynotehub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var noteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Získej databázi
        database = AppDatabase.getDatabase(this)
        noteDao = database.noteDao()

        val editTitle: EditText = findViewById(R.id.editTitle)
        val editContent: EditText = findViewById(R.id.editContent)
        val btnAdd: Button = findViewById(R.id.btnAdd)
        val textNotes: TextView = findViewById(R.id.textNotes)

        // Přidání poznámky
        btnAdd.setOnClickListener {
            val title = editTitle.text.toString()
            val content = editContent.text.toString()

            lifecycleScope.launch {
                noteDao.insert(Note(title = title, content = content, category = "Osobní"))
                editTitle.text.clear()
                editContent.text.clear()
            }
        }

        // Zobrazení poznámek (live update)
        lifecycleScope.launch {
            noteDao.getAllNotes().collect { notes ->
                val text = notes.joinToString("\n\n") {
                    "${it.title}\n${it.content}\n[${it.category}]"
                }
                textNotes.text = text
            }
        }
    }
}

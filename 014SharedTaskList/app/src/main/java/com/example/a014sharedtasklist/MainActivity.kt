package com.example.a014sharedtasklist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var editTask: EditText
    private lateinit var btnAdd: Button
    private lateinit var listView: ListView
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore

        editTask = findViewById(R.id.editTask)
        btnAdd = findViewById(R.id.btnAdd)
        listView = findViewById(R.id.listView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        btnAdd.setOnClickListener { addTask() }
        listView.setOnItemClickListener { _, _, position, _ -> toggleTask(position) }
        listView.setOnItemLongClickListener { _, _, position, _ -> deleteTask(position); true }

        loadTasks()
    }

    private fun addTask() {
        val title = editTask.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Zadej název úkolu!", Toast.LENGTH_SHORT).show()
            return
        }

        val task = hashMapOf(
            "title" to title,
            "isCompleted" to false,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("tasks").add(task)
            .addOnSuccessListener {
                editTask.text.clear()
                Toast.makeText(this, "Úkol přidán!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Chyba: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTasks() {
        db.collection("tasks")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Chyba: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                tasks.clear()
                adapter.clear()

                snapshot?.documents?.forEach { doc ->
                    val task = Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        isCompleted = doc.getBoolean("isCompleted") ?: false,
                        timestamp = doc.getLong("timestamp") ?: 0
                    )
                    tasks.add(task)
                    adapter.add(if (task.isCompleted) "✓ ${task.title}" else "○ ${task.title}")
                }
            }
    }

    private fun toggleTask(position: Int) {
        val task = tasks[position]
        db.collection("tasks").document(task.id)
            .update("isCompleted", !task.isCompleted)
    }

    private fun deleteTask(position: Int) {
        val task = tasks[position]
        db.collection("tasks").document(task.id).delete()
        Toast.makeText(this, "Úkol smazán!", Toast.LENGTH_SHORT).show()
    }
}

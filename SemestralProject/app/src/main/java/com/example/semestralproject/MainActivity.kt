package com.example.semestralproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.semestralproject.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val workoutList = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter
    private var lastDeletedWorkout: Workout? = null
    private var lastDeletedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Moje tréninky"

        loadWorkoutsFromPreferences()
        setupAdapter()
        setupListeners()
        updateEmptyState()
    }

    private fun setupAdapter() {
        adapter = WorkoutAdapter(
            workoutList,
            onItemClick = { workout ->
                val intent = Intent(this, WorkoutDetailActivity::class.java)
                intent.putExtra("WORKOUT", workout)
                startActivityForResult(intent, REQUEST_CODE_DETAIL)
            },
            onDeleteClick = { workout, position ->
                deleteWorkout(workout, position)
            },
            onCheckboxChange = { workout, isChecked ->
                workout.isCompleted = isChecked
                saveWorkoutsToPreferences()
                Toast.makeText(this, "Trénink ${if (isChecked) "dokončen" else "nedokončen"}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.listViewWorkouts.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnAddWorkout.setOnClickListener {
            val intent = Intent(this, AddWorkoutActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }
    }

    private fun deleteWorkout(workout: Workout, position: Int) {
        lastDeletedWorkout = workout
        lastDeletedPosition = position

        workoutList.removeAt(position)
        adapter.notifyDataSetChanged()
        saveWorkoutsToPreferences()
        updateEmptyState()

        Snackbar.make(binding.root, "Trénink smazán", Snackbar.LENGTH_LONG)
            .setAction("Vrátit") {
                lastDeletedWorkout?.let {
                    workoutList.add(lastDeletedPosition, it)
                    adapter.notifyDataSetChanged()
                    saveWorkoutsToPreferences()
                    updateEmptyState()
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD -> {
                    data?.getSerializableExtra("NEW_WORKOUT")?.let { workout ->
                        workoutList.add(workout as Workout)
                        adapter.notifyDataSetChanged()
                        saveWorkoutsToPreferences()
                        updateEmptyState()

                        val customToast = layoutInflater.inflate(R.layout.custom_toast, null)
                        Toast(this).apply {
                            duration = Toast.LENGTH_SHORT
                            view = customToast
                            show()
                        }
                    }
                }
                REQUEST_CODE_DETAIL -> {
                    data?.getSerializableExtra("UPDATED_WORKOUT")?.let { updated ->
                        val updatedWorkout = updated as Workout
                        val index = workoutList.indexOfFirst { it.id == updatedWorkout.id }
                        if (index != -1) {
                            workoutList[index] = updatedWorkout
                            adapter.notifyDataSetChanged()
                            saveWorkoutsToPreferences()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
                binding.fragmentContainer.visibility = android.view.View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEmptyState() {
        if (workoutList.isEmpty()) {
            binding.tvEmptyState.visibility = android.view.View.VISIBLE
            binding.listViewWorkouts.visibility = android.view.View.GONE
        } else {
            binding.tvEmptyState.visibility = android.view.View.GONE
            binding.listViewWorkouts.visibility = android.view.View.VISIBLE
        }
    }

    private fun saveWorkoutsToPreferences() {
        val prefs = getSharedPreferences("workouts_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val workoutStrings = workoutList.map { workout ->
            "${workout.id}|${workout.name}|${workout.description}|${workout.intensity}|${workout.duration}|${workout.date}|${workout.isCompleted}|${workout.imageUri ?: ""}"
        }

        editor.putString("workouts", workoutStrings.joinToString(";;;"))
        editor.apply()
    }

    private fun loadWorkoutsFromPreferences() {
        val prefs = getSharedPreferences("workouts_prefs", MODE_PRIVATE)
        val workoutsString = prefs.getString("workouts", "") ?: ""

        if (workoutsString.isNotEmpty()) {
            workoutList.clear()
            workoutsString.split(";;;").forEach { workoutString ->
                val parts = workoutString.split("|")
                if (parts.size >= 7) {
                    val workout = Workout(
                        id = parts[0].toInt(),
                        name = parts[1],
                        description = parts[2],
                        intensity = parts[3],
                        duration = parts[4].toInt(),
                        date = parts[5],
                        isCompleted = parts[6].toBoolean(),
                        imageUri = parts.getOrNull(7)?.takeIf { it.isNotEmpty() }
                    )
                    workoutList.add(workout)
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1
        const val REQUEST_CODE_DETAIL = 2
    }
}

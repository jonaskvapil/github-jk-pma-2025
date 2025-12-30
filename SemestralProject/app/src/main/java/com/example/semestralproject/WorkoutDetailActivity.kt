package com.example.semestralproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.semestralproject.databinding.ActivityWorkoutDetailBinding
import com.google.android.material.snackbar.Snackbar

class WorkoutDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutDetailBinding
    private lateinit var workout: Workout
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivDetailImage.setImageURI(it)
            binding.ivDetailImage.visibility = android.view.View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workout = intent.getSerializableExtra("WORKOUT") as Workout

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail tréninku"

        loadWorkoutData()
        setupListeners()
    }

    private fun loadWorkoutData() {
        binding.etDetailName.setText(workout.name)
        binding.etDetailDescription.setText(workout.description)
        binding.etDetailDuration.setText(workout.duration.toString())
        binding.tvDetailDate.text = "Datum: ${workout.date}"
        binding.cbDetailCompleted.isChecked = workout.isCompleted

        when (workout.intensity) {
            "Nízká" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailLow)
            "Střední" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailMedium)
            "Vysoká" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailHigh)
        }

        workout.imageUri?.let {
            binding.ivDetailImage.setImageURI(Uri.parse(it))
            binding.ivDetailImage.visibility = android.view.View.VISIBLE
            selectedImageUri = Uri.parse(it)
        }
    }

    private fun setupListeners() {
        binding.btnChangeImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnUpdateWorkout.setOnClickListener {
            updateWorkout()
        }

        binding.btnDeleteWorkout.setOnClickListener {
            Snackbar.make(binding.root, "Opravdu smazat trénink?", Snackbar.LENGTH_LONG)
                .setAction("Ano") {
                    deleteWorkout()
                }
                .show()
        }
    }

    private fun updateWorkout() {
        val name = binding.etDetailName.text.toString().trim()
        val description = binding.etDetailDescription.text.toString().trim()
        val durationStr = binding.etDetailDuration.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Zadejte název tréninku", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = durationStr.toIntOrNull() ?: 0
        if (duration <= 0) {
            Toast.makeText(this, "Zadejte platnou délku trvání", Toast.LENGTH_SHORT).show()
            return
        }

        val intensity = when (binding.radioGroupDetailIntensity.checkedRadioButtonId) {
            R.id.radioDetailLow -> "Nízká"
            R.id.radioDetailMedium -> "Střední"
            R.id.radioDetailHigh -> "Vysoká"
            else -> workout.intensity
        }

        workout.name = name
        workout.description = description
        workout.duration = duration
        workout.intensity = intensity
        workout.isCompleted = binding.cbDetailCompleted.isChecked
        workout.imageUri = selectedImageUri?.toString()

        val resultIntent = Intent()
        resultIntent.putExtra("UPDATED_WORKOUT", workout)
        setResult(RESULT_OK, resultIntent)

        Toast.makeText(this, "Trénink aktualizován", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun deleteWorkout() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

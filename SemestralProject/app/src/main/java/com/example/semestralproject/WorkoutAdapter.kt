package com.example.semestralproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

class WorkoutAdapter(
    private val workouts: List<Workout>,
    private val onItemClick: (Workout) -> Unit,
    private val onDeleteClick: (Workout, Int) -> Unit,
    private val onCheckboxChange: (Workout, Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = workouts.size

    override fun getItem(position: Int): Any = workouts[position]

    override fun getItemId(position: Int): Long = workouts[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_workout, parent, false)

        val workout = workouts[position]

        val tvName = view.findViewById<TextView>(R.id.tvWorkoutName)
        val tvDate = view.findViewById<TextView>(R.id.tvWorkoutDate)
        val tvDuration = view.findViewById<TextView>(R.id.tvWorkoutDuration)
        val tvIntensity = view.findViewById<TextView>(R.id.tvWorkoutIntensity)
        val cbCompleted = view.findViewById<CheckBox>(R.id.cbWorkoutCompleted)
        val ivImage = view.findViewById<ImageView>(R.id.ivWorkoutThumb)
        val btnDelete = view.findViewById<Button>(R.id.btnDeleteWorkout)

        tvName.text = workout.name
        tvDate.text = workout.date
        tvDuration.text = "${workout.duration} min"
        tvIntensity.text = workout.intensity

        when (workout.intensity) {
            "Nízká" -> tvIntensity.setTextColor(ContextCompat.getColor(view.context, R.color.intensity_low))
            "Střední" -> tvIntensity.setTextColor(ContextCompat.getColor(view.context, R.color.intensity_medium))
            "Vysoká" -> tvIntensity.setTextColor(ContextCompat.getColor(view.context, R.color.intensity_high))
        }

        cbCompleted.isChecked = workout.isCompleted
        cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            onCheckboxChange(workout, isChecked)
        }

        workout.imageUri?.let {
            ivImage.setImageURI(Uri.parse(it))
            ivImage.visibility = View.VISIBLE
        } ?: run {
            ivImage.visibility = View.GONE
        }

        view.setOnClickListener {
            onItemClick(workout)
        }

        btnDelete.setOnClickListener {
            onDeleteClick(workout, position)
        }

        return view
    }
}

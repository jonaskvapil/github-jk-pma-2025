package com.example.semestralproject

import android.net.Uri
import java.io.Serializable

data class Workout(
    val id: Int,
    var name: String,
    var description: String,
    var intensity: String,
    var duration: Int,
    var date: String,
    var isCompleted: Boolean = false,
    var imageUri: String? = null
) : Serializable

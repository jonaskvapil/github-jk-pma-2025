package com.example.ukol002

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        val etDistance: EditText = findViewById(R.id.etDistance)
        val etTimeMin: EditText = findViewById(R.id.etTimeMin)
        val btnCalc: Button = findViewById(R.id.btnCalc)
        val btnClear: Button = findViewById(R.id.btnClear)
        val tvResult: TextView = findViewById(R.id.tvResult)

        btnCalc.setOnClickListener {
            val dist = etDistance.text.toString().replace(',', '.').toDoubleOrNull()
            val timeMin = etTimeMin.text.toString().replace(',', '.').toDoubleOrNull()

            if (dist == null || dist <= 0) {
                toast("Zadej vzdálenost v km (>0)."); return@setOnClickListener
            }
            if (timeMin == null || timeMin <= 0) {
                toast("Zadej čas v minutách (>0)."); return@setOnClickListener
            }

            val paceMinPerKm = timeMin / dist
            val paceMin = floor(paceMinPerKm).toInt()
            val paceSec = ((paceMinPerKm - paceMin) * 60).toInt().coerceIn(0, 59)
            val speedKmh = (dist / (timeMin / 60.0))

            tvResult.text = "Tempo: %d:%02d min/km\nRychlost: %.2f km/h"
                .format(paceMin, paceSec, speedKmh)
        }

        btnClear.setOnClickListener {
            etDistance.text.clear()
            etTimeMin.text.clear()
            tvResult.text = "Zadej údaje a dej Vypočítat."
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

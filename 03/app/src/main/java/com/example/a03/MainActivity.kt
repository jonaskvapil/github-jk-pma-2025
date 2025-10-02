package com.example.a03

import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a03.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = "Objednavka kola"
        //binding settings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOrder.setOnClickListener {
            //nacteni ID vybraneho radioButtonu z radioGroup
            val bikeRbId = binding.rgBikes.checkedRadioButtonId
            val bike = findViewById<RadioButton>(bikeRbId)
            val fork = binding.cbFork.isChecked
            val saddle = binding.cbSaddle.isChecked
            val handleBar = binding.cbHandleBar.isChecked

            val ordedText = "Souhrn objednávky" + "${bike.text}" +
            (if(fork)";lepsi vidlice" else "") +
                    (if(saddle)"; lepsi sedlo" else "") +
                    (if(handleBar)"; lepsi riditka" else "")
            binding.tvOrder.text = ordedText
        }

        //Zmena obrazku v zavislosti na vybranem radiobuttonu
        binding.rbBike1.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.oz1)
        }
        binding.rbBike2.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.oz2)
        }
        binding.rbBike3.setOnClickListener {
            binding.ivBike.setImageResource(R.drawable.oz3)
        }
    }
}
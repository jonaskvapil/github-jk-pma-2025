package com.example.a011

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a011.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: String = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState == null) {
            loadHomeFragment()
        }

        binding.btnHome.setOnClickListener {
            if (currentFragment != "home") {
                loadHomeFragment()
                Toast.makeText(this, "Domovská obrazovka", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnProfile.setOnClickListener {
            if (currentFragment != "profile") {
                loadProfileFragment()
                Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddEntry.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HomeFragment())
            .commit()
        currentFragment = "home"
    }

    private fun loadProfileFragment() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, ProfileFragment())
            .commit()
        currentFragment = "profile"
    }

    override fun onResume() {
        super.onResume()
        if (currentFragment == "home") {
            loadHomeFragment()
        }
    }
}

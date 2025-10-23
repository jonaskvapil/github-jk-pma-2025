package com.example.a009

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a009.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri: Uri? -> binding.ivImage.setImageURI(uri)
        }

        binding.btnTakeImage.setOnClickListener {
            // You must specify the MIME type of the content to get, for example, "image/*" for any image type.
            getContent.launch("image/*")
        }
    }
}
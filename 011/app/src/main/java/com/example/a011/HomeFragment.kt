package com.example.a011

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.a011.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        val prefs = requireContext().getSharedPreferences("diary_prefs", Context.MODE_PRIVATE)

        // Načtení jména uživatele
        val userName = prefs.getString("user_name", null)
        if (userName != null) {
            binding.tvWelcome.text = "Vítej, $userName!"
        }

        // Načtení počtu záznamů
        val entriesCount = prefs.getInt("entries_count", 0)
        binding.tvEntriesCount.text = "Počet záznamů: $entriesCount"

        // Načtení posledního záznamu
        val lastEntry = prefs.getString("last_entry_title", null)
        binding.tvLastEntry.text = when {
            lastEntry != null -> "Poslední záznam: $lastEntry"
            else -> "Poslední záznam: Žádný"
        }

        // Načtení URI obrázku, pokud existuje
        val imageUriString = prefs.getString("last_entry_image", null)
        if (imageUriString != null) {
            binding.ivHomeImage.setImageURI(android.net.Uri.parse(imageUriString))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

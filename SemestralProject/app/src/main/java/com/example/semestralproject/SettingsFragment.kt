package com.example.semestralproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.semestralproject.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

        binding.cbShowOnlyIncomplete.isChecked = prefs.getBoolean("show_only_incomplete", false)
        binding.cbDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        binding.cbNotifications.isChecked = prefs.getBoolean("notifications", true)
    }

    private fun setupListeners() {
        binding.cbShowOnlyIncomplete.setOnCheckedChangeListener { _, isChecked ->
            savePreference("show_only_incomplete", isChecked)
            Toast.makeText(context, "Filtr ${if (isChecked) "zapnut" else "vypnut"}", Toast.LENGTH_SHORT).show()
        }

        binding.cbDarkMode.setOnCheckedChangeListener { _, isChecked ->
            savePreference("dark_mode", isChecked)
            Toast.makeText(context, "Tmavý režim ${if (isChecked) "zapnut" else "vypnut"}", Toast.LENGTH_SHORT).show()
        }

        binding.cbNotifications.setOnCheckedChangeListener { _, isChecked ->
            savePreference("notifications", isChecked)
            Toast.makeText(context, "Notifikace ${if (isChecked) "zapnuty" else "vypnuty"}", Toast.LENGTH_SHORT).show()
        }

        binding.btnCloseSettings.setOnClickListener {
            parentFragmentManager.popBackStack()
            requireActivity().findViewById<View>(R.id.fragmentContainer).visibility = View.GONE
        }
    }

    private fun savePreference(key: String, value: Boolean) {
        val prefs = requireContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

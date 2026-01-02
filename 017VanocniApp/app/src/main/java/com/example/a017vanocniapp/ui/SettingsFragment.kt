package com.example.a017vanocniapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.a017vanocniapp.R
import com.example.a017vanocniapp.databinding.FragmentSettingsBinding
import com.example.a017vanocniapp.datastore.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStore = SettingsDataStore(requireContext())

        loadSettings()
        setupListeners()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnOpenGame.setOnClickListener {
            findNavController().navigate(R.id.gameFragment)
        }
    }

    private fun loadSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.isDarkMode.collectLatest { isDark ->
                binding.switchDarkMode.isChecked = isDark
                applyTheme(isDark)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.countdownVisible.collectLatest { countdown ->
                binding.switchCountdown.isChecked = countdown
            }
        }
    }

    private fun applyTheme(isDark: Boolean) {
        if (isDark) {
            binding.root.setBackgroundColor(Color.parseColor("#1a1a1a"))
            binding.textTitle.setTextColor(Color.WHITE)
            binding.textDarkLabel.setTextColor(Color.WHITE)
            binding.textCountdownLabel.setTextColor(Color.WHITE)
        } else {
            binding.root.setBackgroundColor(Color.parseColor("#f5f5f5"))
            binding.textTitle.setTextColor(Color.parseColor("#c41e3a"))
            binding.textDarkLabel.setTextColor(Color.BLACK)
            binding.textCountdownLabel.setTextColor(Color.BLACK)
        }
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.saveDarkMode(isChecked)
            }
        }

        binding.switchCountdown.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.saveCountdownVisible(isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

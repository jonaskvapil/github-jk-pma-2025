package com.example.a017vanocniapp.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.a017vanocniapp.databinding.FragmentHomeBinding
import com.example.a017vanocniapp.datastore.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore
    private val handler = Handler(Looper.getMainLooper())
    private var countdownRunnable: Runnable? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Uložíme persistentní přístup k URI
            requireActivity().contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            binding.imageChristmasTree.setImageURI(it)

            viewLifecycleOwner.lifecycleScope.launch {
                settingsDataStore.saveChristmasPhoto(it.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStore = SettingsDataStore(requireContext())

        binding.btnSelectPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        observeDataStore()
        startCountdown()
    }

    private fun observeDataStore() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.isDarkMode.collectLatest { isDark ->
                applyTheme(isDark)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.countdownVisible.collectLatest { visible ->
                binding.textCountdown.visibility = if (visible) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.christmasPhotoUri.collectLatest { uriString ->
                if (uriString != null) {
                    try {
                        binding.imageChristmasTree.setImageURI(Uri.parse(uriString))
                    } catch (e: Exception) {
                        // Fotka není dostupná
                    }
                }
            }
        }
    }

    private fun applyTheme(isDark: Boolean) {
        if (isDark) {
            binding.root.setBackgroundColor(Color.parseColor("#1a1a1a"))
            binding.textTitle.setTextColor(Color.WHITE)
            binding.textDarkMode.setTextColor(Color.WHITE)
            binding.textCountdown.setTextColor(Color.parseColor("#FFD700"))
            binding.textDarkMode.setBackgroundColor(Color.parseColor("#2d2d2d"))
            binding.textCountdown.setBackgroundColor(Color.parseColor("#2d2d2d"))
            binding.imageChristmasTree.setBackgroundColor(Color.parseColor("#2d2d2d"))
            binding.textDarkMode.text = "🌙 Tmavý mód: ZAPNUTÝ"
        } else {
            binding.root.setBackgroundColor(Color.parseColor("#f5f5f5"))
            binding.textTitle.setTextColor(Color.parseColor("#c41e3a"))
            binding.textDarkMode.setTextColor(Color.BLACK)
            binding.textCountdown.setTextColor(Color.parseColor("#165B33"))
            binding.textDarkMode.setBackgroundColor(Color.parseColor("#E0E0E0"))
            binding.textCountdown.setBackgroundColor(Color.parseColor("#FFE5E5"))
            binding.imageChristmasTree.setBackgroundColor(Color.parseColor("#E0E0E0"))
            binding.textDarkMode.text = "☀️ Tmavý mód: VYPNUTÝ"
        }
    }

    private fun startCountdown() {
        countdownRunnable = object : Runnable {
            override fun run() {
                val now = Calendar.getInstance()
                val christmas = Calendar.getInstance().apply {
                    set(Calendar.MONTH, Calendar.DECEMBER)
                    set(Calendar.DAY_OF_MONTH, 25)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)

                    if (timeInMillis < now.timeInMillis) {
                        add(Calendar.YEAR, 1)
                    }
                }

                val diff = christmas.timeInMillis - now.timeInMillis

                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

                binding.textCountdown.text = "🎄 Do Vánoc zbývá:\n$days dní, $hours hodin, $minutes minut, $seconds sekund"

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(countdownRunnable!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownRunnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }
}

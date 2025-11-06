package com.example.a011

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.a011.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher pro výběr obrázku
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivProfileImage.setImageURI(it)
            Toast.makeText(requireContext(), "Obrázek vybrán", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()

        // Výběr profilového obrázku
        binding.btnSelectProfileImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Uložení profilu
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("diary_prefs", Context.MODE_PRIVATE)

        val userName = prefs.getString("user_name", "")
        binding.etProfileName.setText(userName)

        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
        binding.cbNotifications.isChecked = notificationsEnabled

        val profileImageUri = prefs.getString("profile_image_uri", null)
        if (profileImageUri != null) {
            selectedImageUri = Uri.parse(profileImageUri)
            binding.ivProfileImage.setImageURI(selectedImageUri)
        }
    }

    private fun saveProfile() {
        val name = binding.etProfileName.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Zadejte jméno!", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("diary_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("user_name", name)
        editor.putBoolean("notifications_enabled", binding.cbNotifications.isChecked)

        selectedImageUri?.let {
            editor.putString("profile_image_uri", it.toString())
        }

        editor.apply()

        // Snackbar s UNDO možností
        Snackbar.make(binding.root, "Profil uložen!", Snackbar.LENGTH_LONG)
            .setAction("ZPĚT") {
                // UNDO akce
                binding.etProfileName.setText("")
                Toast.makeText(requireContext(), "Zrušeno", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

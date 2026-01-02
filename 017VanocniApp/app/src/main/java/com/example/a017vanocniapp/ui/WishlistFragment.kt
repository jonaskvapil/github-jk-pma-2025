package com.example.a017vanocniapp.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.a017vanocniapp.databinding.FragmentWishlistBinding
import com.example.a017vanocniapp.datastore.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var adapter: ArrayAdapter<String>
    private val items = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStore = SettingsDataStore(requireContext())

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        binding.listViewWishlist.adapter = adapter

        binding.btnAddItem.setOnClickListener {
            addItem()
        }

        binding.btnClearAll.setOnClickListener {
            clearAll()
        }

        binding.listViewWishlist.setOnItemLongClickListener { _, _, position, _ ->
            removeItem(position)
            true
        }

        observeWishlist()
        observeDarkMode()
    }

    private fun addItem() {
        val item = binding.editItem.text.toString().trim()
        if (item.isEmpty()) {
            Toast.makeText(requireContext(), "Zadej dárek!", Toast.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.addWishlistItem("🎁 $item")
            binding.editItem.text.clear()
            Toast.makeText(requireContext(), "Přidáno!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItem(position: Int) {
        val item = items[position]
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.removeWishlistItem(item)
            Toast.makeText(requireContext(), "Smazáno!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearAll() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.clearWishlist()
            Toast.makeText(requireContext(), "Vše smazáno!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeWishlist() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.wishlistItems.collectLatest { wishlist ->
                items.clear()
                items.addAll(wishlist)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeDarkMode() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsDataStore.isDarkMode.collectLatest { isDark ->
                if (isDark) {
                    binding.root.setBackgroundColor(Color.parseColor("#1a1a1a"))
                    binding.textTitle.setTextColor(Color.WHITE)
                    binding.editItem.setTextColor(Color.WHITE)
                    binding.editItem.setHintTextColor(Color.GRAY)
                } else {
                    binding.root.setBackgroundColor(Color.parseColor("#f5f5f5"))
                    binding.textTitle.setTextColor(Color.parseColor("#c41e3a"))
                    binding.editItem.setTextColor(Color.BLACK)
                    binding.editItem.setHintTextColor(Color.GRAY)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

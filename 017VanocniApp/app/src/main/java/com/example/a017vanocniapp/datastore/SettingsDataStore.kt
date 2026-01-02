package com.example.a017vanocniapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val isDarkMode = booleanPreferencesKey("is_dark_mode")
    val countdownVisible = booleanPreferencesKey("countdown_visible")
    val wishlistItems = stringPreferencesKey("wishlist_items")
    val christmasPhotoUri = stringPreferencesKey("christmas_photo_uri")
    val quizHighScore = stringPreferencesKey("quiz_high_score")


}

class SettingsDataStore(private val context: Context) {
    val isDarkMode: Flow<Boolean> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it[PreferencesKeys.isDarkMode] ?: false }

    val countdownVisible: Flow<Boolean> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it[PreferencesKeys.countdownVisible] ?: true }

    val wishlistItems: Flow<List<String>> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val itemsString = it[PreferencesKeys.wishlistItems] ?: ""
            if (itemsString.isEmpty()) emptyList() else itemsString.split("||")
        }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.isDarkMode] = enabled
        }
    }

    suspend fun saveCountdownVisible(visible: Boolean) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.countdownVisible] = visible
        }
    }

    suspend fun addWishlistItem(item: String) {
        context.settingsDataStore.edit { settings ->
            val current = settings[PreferencesKeys.wishlistItems] ?: ""
            val items = if (current.isEmpty()) mutableListOf() else current.split("||").toMutableList()
            items.add(item)
            settings[PreferencesKeys.wishlistItems] = items.joinToString("||")
        }
    }

    suspend fun removeWishlistItem(item: String) {
        context.settingsDataStore.edit { settings ->
            val current = settings[PreferencesKeys.wishlistItems] ?: ""
            if (current.isNotEmpty()) {
                val items = current.split("||").toMutableList()
                items.remove(item)
                settings[PreferencesKeys.wishlistItems] = items.joinToString("||")
            }
        }
    }

    suspend fun clearWishlist() {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.wishlistItems] = ""
        }
    }
    val christmasPhotoUri: Flow<String?> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it[PreferencesKeys.christmasPhotoUri] }

    suspend fun saveChristmasPhoto(uri: String) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.christmasPhotoUri] = uri
        }
    }

    val quizHighScore: Flow<Int> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it[PreferencesKeys.quizHighScore]?.toIntOrNull() ?: 0 }


    suspend fun saveQuizHighScore(score: Int) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.quizHighScore] = score.toString()
        }
    }

}

package com.example.investdiary.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val THEME_KEY = booleanPreferencesKey("theme")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
        val TARGET_AMOUNT_KEY = doublePreferencesKey("target_amount")
        // NOVÉ: Klíč pro název portfolia
        val PORTFOLIO_NAME_KEY = stringPreferencesKey("portfolio_name")
    }

    // Načtení měny (výchozí CZK)
    val currencyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "CZK"
    }

    // Načtení tématu (výchozí false = Light)
    val themeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: false
    }

    // Načtení notifikací
    val notificationsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_KEY] ?: true
    }

    // Načtení cílové částky
    val targetAmountFlow: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[TARGET_AMOUNT_KEY] ?: 100000.0
    }

    // NOVÉ: Načtení názvu portfolia (výchozí "Moje Portfolio")
    val portfolioNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PORTFOLIO_NAME_KEY] ?: "Moje Portfolio"
    }

    // --- Ukládání ---

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[CURRENCY_KEY] = currency }
    }

    suspend fun setTheme(isDark: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = isDark }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }

    suspend fun setTargetAmount(amount: Double) {
        context.dataStore.edit { it[TARGET_AMOUNT_KEY] = amount }
    }

    // NOVÉ: Uložení názvu
    suspend fun setPortfolioName(name: String) {
        context.dataStore.edit { it[PORTFOLIO_NAME_KEY] = name }
    }
}

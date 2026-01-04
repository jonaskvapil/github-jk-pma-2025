package com.example.a013edugame.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Zde registrujeme naše tabulky (entities)
@Database(entities = [GameResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Zpřístupníme naše DAO
    abstract fun gameDao(): GameDao

    companion object {
        // Volatile zajistí, že změny v této proměnné vidí všechna vlákna okamžitě
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Pokud instance existuje, vrať ji. Pokud ne, vytvoř ji.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_database" // Název souboru databáze v telefonu
                )
                    // FallbackDestructiveMigration smaže data, pokud změníš strukturu tabulky (pro vývoj ok)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

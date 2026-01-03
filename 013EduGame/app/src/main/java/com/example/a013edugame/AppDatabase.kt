package com.example.a013edugame

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Question::class, GameResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edu_game_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Přidej testovací otázky
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateDatabase(database.appDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Testovací otázky
        private suspend fun populateDatabase(dao: AppDao) {
            dao.insertQuestion(Question(text = "Kolik je 5 + 3?", correctAnswer = "8",
                option1 = "7", option2 = "8", option3 = "9", option4 = "10"))
            dao.insertQuestion(Question(text = "Hlavní město Česka?", correctAnswer = "Praha",
                option1 = "Brno", option2 = "Praha", option3 = "Ostrava", option4 = "Plzeň"))
            dao.insertQuestion(Question(text = "Kolik je 12 * 2?", correctAnswer = "24",
                option1 = "22", option2 = "24", option3 = "26", option4 = "28"))
            dao.insertQuestion(Question(text = "Největší planeta?", correctAnswer = "Jupiter",
                option1 = "Mars", option2 = "Země", option3 = "Jupiter", option4 = "Saturn"))
            dao.insertQuestion(Question(text = "Kolik je 100 / 4?", correctAnswer = "25",
                option1 = "20", option2 = "25", option3 = "30", option4 = "35"))
        }
    }
}

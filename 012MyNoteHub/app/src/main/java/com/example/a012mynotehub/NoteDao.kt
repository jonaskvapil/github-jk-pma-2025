package com.example.a012mynotehub

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)
}

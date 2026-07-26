package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val level: Int = 1,
    val completedLessons: String = "",
    val completedLabs: String = "",
    val capturedFlags: String = "",
    val streakDays: Int = 1,
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "Général",
    val timestamp: Long = System.currentTimeMillis()
)

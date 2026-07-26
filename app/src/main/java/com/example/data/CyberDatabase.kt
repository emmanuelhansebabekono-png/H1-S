package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserProgressEntity::class, NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CyberDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: CyberDatabase? = null

        fun getDatabase(context: Context): CyberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberDatabase::class.java,
                    "hackguard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

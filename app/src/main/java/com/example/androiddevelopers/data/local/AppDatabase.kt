package com.example.androiddevelopers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androiddevelopers.domain.Question

@Database(entities = [Question::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}
package com.example.androiddevelopers

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androiddevelopers.ui.game.model.Question
import com.example.androiddevelopers.ui.game.model.QuestionDao

@Database(entities = [Question::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}
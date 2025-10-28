package com.example.androiddevelopers.ui.game.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuestionDao {
    // Gets a single random question from a specific category
    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(category: String): Question?
}
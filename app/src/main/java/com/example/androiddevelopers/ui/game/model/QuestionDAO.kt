package com.example.androiddevelopers.ui.game.model

import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuestionDao {

    // Gets a single random question from the table
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): Question?
}
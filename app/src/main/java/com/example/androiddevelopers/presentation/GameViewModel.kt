package com.example.androiddevelopers.presentation

import androidx.lifecycle.ViewModel
import com.example.androiddevelopers.domain.Question
import com.example.androiddevelopers.data.local.QuestionDao

class GameViewModel(private val questionDao: QuestionDao) : ViewModel() {

    // Public properties to hold the game state. The Fragment will read these.
    var isGameActive: Boolean = false
        private set // Can only be changed from within the ViewModel

    var currentQuestion: Question? = null
        private set

    var score: Int = 0
        private set

    private var selectedCategory: String = ""

    fun selectGame(category: String) {
        selectedCategory = category
        score = 0
        isGameActive = true
    }

    // Returns true if the answer was correct, false otherwise
    fun checkAnswer(selectedIndex: Int): Boolean {
        val isCorrect = selectedIndex == currentQuestion?.correctAnswerIndex
        if (isCorrect) {
            score++
        }
        return isCorrect
    }

    // A suspend function to be called from the Fragment's coroutine
    suspend fun loadNewQuestion() {
        currentQuestion = questionDao.getRandomQuestion(selectedCategory)
    }
    /**
     * Finishes the current game session, preserving the score.
     */
    fun finishCurrentGame() {
        isGameActive = false
        currentQuestion = null
    }

    /**
     * Exits the game completely, resetting the score.
     */
    fun exitGame() {
        isGameActive = false
        currentQuestion = null
        selectedCategory = ""
        score = 0 // Score is reset here
    }
}
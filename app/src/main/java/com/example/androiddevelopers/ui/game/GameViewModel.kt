package com.example.androiddevelopers.ui.game

import androidx.lifecycle.ViewModel
import com.example.androiddevelopers.ui.game.model.Question
import com.example.androiddevelopers.ui.game.model.QuestionDao

class GameViewModel(private val questionDao: QuestionDao) : ViewModel() {

    var currentQuestion: Question? = null
    var score = 0

    // suspend function to be called from a coroutine
    suspend fun loadNewQuestion(): Question? {
        currentQuestion = questionDao.getRandomQuestion()
        return currentQuestion
    }

    fun checkAnswer(selectedIndex: Int): Boolean {
        val isCorrect = selectedIndex == currentQuestion?.correctAnswerIndex
        if (isCorrect) {
            score++
        }
        return isCorrect
    }
}
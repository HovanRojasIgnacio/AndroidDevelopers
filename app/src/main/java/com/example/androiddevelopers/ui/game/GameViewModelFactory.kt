package com.example.androiddevelopers.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androiddevelopers.ui.game.model.QuestionDao

class GameViewModelFactory(private val questionDao: QuestionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(questionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
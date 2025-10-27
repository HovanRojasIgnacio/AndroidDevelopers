package com.example.androiddevelopers.ui.game

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.androiddevelopers.DeveloperApp
import com.example.androiddevelopers.R
import kotlinx.coroutines.launch

class GameFragment : Fragment(R.layout.fragment_game) {

    // Get the Application instance to access the database [cite: 1197, 1203]
    private val application by lazy { requireActivity().application as DeveloperApp }

    // Use the factory to create the ViewModel with the DAO [cite: 1203]
    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(application.database.questionDao())
    }

    private lateinit var textScore: TextView
    private lateinit var textQuestion: TextView
    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views using view.findViewById [cite: 633]
        textScore = view.findViewById(R.id.text_score)
        textQuestion = view.findViewById(R.id.text_question)
        buttonA = view.findViewById(R.id.button_option_a)
        buttonB = view.findViewById(R.id.button_option_b)
        buttonC = view.findViewById(R.id.button_option_c)

        // Set listeners for answer buttons
        buttonA.setOnClickListener { handleAnswer(0) }
        buttonB.setOnClickListener { handleAnswer(1) }
        buttonC.setOnClickListener { handleAnswer(2) }

        // Load the first question
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        // Use a coroutine to call the suspend function [cite: 1021, 1037]
        viewLifecycleOwner.lifecycleScope.launch {
            val question = viewModel.loadNewQuestion()
            if (question != null) {
                textQuestion.text = question.questionText
                buttonA.text = question.optionA
                buttonB.text = question.optionB
                buttonC.text = question.optionC
            } else {
                textQuestion.text = "No more questions!"
                buttonA.visibility = View.GONE
            }
            textScore.text = "Score: ${viewModel.score}"
        }
    }

    private fun handleAnswer(selectedIndex: Int) {
        val isCorrect = viewModel.checkAnswer(selectedIndex)
        if (isCorrect) {
            Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Incorrect!", Toast.LENGTH_SHORT).show()
        }
        // Load the next question after answering
        loadNextQuestion()
    }
}
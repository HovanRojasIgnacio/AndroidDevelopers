package com.example.androiddevelopers.ui.game

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.androiddevelopers.DeveloperApp
import com.example.androiddevelopers.R
import com.example.androiddevelopers.presentation.GameViewModel
import com.example.androiddevelopers.presentation.GameViewModelFactory
import kotlinx.coroutines.launch

class GameFragment : Fragment(R.layout.fragment_game) {

    private val application by lazy { requireActivity().application as DeveloperApp }
    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(application.database.questionDao())
    }

    // UI containers
    private lateinit var gameSelectionContainer: View
    private lateinit var triviaContainer: NestedScrollView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find all views using view.findViewById
        gameSelectionContainer = view.findViewById(R.id.game_selection_container)
        triviaContainer = view.findViewById(R.id.trivia_container)
        val buttonHistoryTrivia = view.findViewById<Button>(R.id.button_history_trivia)
        val buttonGeographyTrivia = view.findViewById<Button>(R.id.button_geography_quiz)
        val buttonFinishGame = view.findViewById<Button>(R.id.button_finish_game)
        val buttonA = view.findViewById<Button>(R.id.button_option_a)
        val buttonB = view.findViewById<Button>(R.id.button_option_b)
        val buttonC = view.findViewById<Button>(R.id.button_option_c)

        // Set listeners for game selection buttons
        buttonHistoryTrivia.setOnClickListener { startGame("history") }
        buttonGeographyTrivia.setOnClickListener { startGame("geography") }

        // Set listeners for trivia answer buttons
        buttonA.setOnClickListener { handleAnswer(0) }
        buttonB.setOnClickListener { handleAnswer(1) }
        buttonC.setOnClickListener { handleAnswer(2) }

        // Set listener for the new finish button
        buttonFinishGame.setOnClickListener {
            viewModel.finishCurrentGame() // Call the new ViewModel function
            updateUi() // Refresh the UI to show the selection screen
        }

        // Immediately update the UI based on the ViewModel's state.
        updateUi()

        // Handle the back button to exit an active game completely (resets score)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isGameActive) {
                    viewModel.exitGame()
                    updateUi() // Go back to the selection screen
                } else {
                    // If no game is active, let the default back action happen
                    isEnabled = false
                }
            }
        })
    }

    private fun startGame(category: String) {
        viewModel.selectGame(category)
        loadNextQuestion()
    }

    private fun handleAnswer(selectedIndex: Int) {
        val isCorrect = viewModel.checkAnswer(selectedIndex)
        val message = if (isCorrect) "Correct!" else "Incorrect!"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadNewQuestion()
            updateUi()
        }
    }

    // This single function is responsible for synchronizing the UI with the ViewModel's state
    private fun updateUi() {
        if (viewModel.isGameActive) {
            gameSelectionContainer.visibility = View.GONE
            triviaContainer.visibility = View.VISIBLE

            // Find views again here to ensure they are not null after view recreation
            val textScore = view?.findViewById<TextView>(R.id.text_score)
            val textQuestion = view?.findViewById<TextView>(R.id.text_question)
            val buttonA = view?.findViewById<Button>(R.id.button_option_a)
            val buttonB = view?.findViewById<Button>(R.id.button_option_b)
            val buttonC = view?.findViewById<Button>(R.id.button_option_c)

            textScore?.text = "Score: ${viewModel.score}"
            val question = viewModel.currentQuestion

            if (question != null) {
                textQuestion?.text = question.questionText
                buttonA?.text = question.optionA
                buttonB?.text = question.optionB
                buttonC?.text = question.optionC
                buttonA?.visibility = View.VISIBLE
                buttonB?.visibility = View.VISIBLE
                buttonC?.visibility = View.VISIBLE
            } else {
                // Handle the end of the game (no more questions)
                textQuestion?.text = "Game Over! Final Score: ${viewModel.score}"
                buttonA?.visibility = View.GONE
                buttonB?.visibility = View.GONE
                buttonC?.visibility = View.GONE
            }
        } else {
            // Show the game selection menu
            gameSelectionContainer.visibility = View.VISIBLE
            triviaContainer.visibility = View.GONE

            // Logic to show the last score
            val textLastScore = view?.findViewById<TextView>(R.id.text_last_score)
            if (viewModel.score > 0) {
                textLastScore?.text = "Last Score: ${viewModel.score}"
                textLastScore?.visibility = View.VISIBLE
            } else {
                textLastScore?.visibility = View.GONE
            }
        }
    }
}
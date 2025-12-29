package com.example.androiddevelopers.ui.game

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class GameFragment : Fragment(R.layout.fragment_game) {

    private val application by lazy { requireActivity().application as DeveloperApp }
    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory(application.database.questionDao())
    }

    // UI containers
    private lateinit var authContainer: View
    private lateinit var gameSelectionContainer: View
    private lateinit var triviaContainer: NestedScrollView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    //ranking
    private lateinit var rankingContainer: View
    private var isRankingActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        // 1. Inicializar Contenedores
        authContainer = view.findViewById(R.id.auth_container)
        gameSelectionContainer = view.findViewById(R.id.game_selection_container)
        triviaContainer = view.findViewById(R.id.trivia_container)
        rankingContainer = view.findViewById(R.id.ranking_container)

        // 2. Vistas de Autenticación
        val btnLogin = view.findViewById<Button>(R.id.button_login)
        val btnRegister = view.findViewById<Button>(R.id.button_register)
        val emailApp = view.findViewById<EditText>(R.id.edit_email)
        val passApp = view.findViewById<EditText>(R.id.edit_password)


        // 3. Vistas de Selección y Juego
        val buttonHistoryTrivia = view.findViewById<Button>(R.id.button_history_trivia)
        val buttonGeographyTrivia = view.findViewById<Button>(R.id.button_geography_quiz)
        val buttonFinishGame = view.findViewById<Button>(R.id.button_finish_game)
        val buttonA = view.findViewById<Button>(R.id.button_option_a)
        val buttonB = view.findViewById<Button>(R.id.button_option_b)
        val buttonC = view.findViewById<Button>(R.id.button_option_c)

        val buttonShowRanking = view.findViewById<Button>(R.id.button_show_ranking)
        val buttonBackFromRanking = view.findViewById<Button>(R.id.button_back_from_ranking)


        // Login
        btnLogin.setOnClickListener {
            val email = emailApp.text.toString()
            val password = passApp.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) updateUi()
                    else Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Registro
        btnRegister.setOnClickListener {
            val email = emailApp.text.toString()
            val password = passApp.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Cuenta creada", Toast.LENGTH_SHORT).show()
                        updateUi()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Selección de juego
        buttonHistoryTrivia.setOnClickListener { startGame("history") }
        buttonGeographyTrivia.setOnClickListener { startGame("geography") }

        // Respuestas
        buttonA.setOnClickListener { handleAnswer(0) }
        buttonB.setOnClickListener { handleAnswer(1) }
        buttonC.setOnClickListener { handleAnswer(2) }

        // Finalizar juego (guarda en Firestore)
        buttonFinishGame.setOnClickListener {
            saveScoreToFirebase()
            viewModel.finishCurrentGame()
            updateUi()
        }

        // Ranking
        buttonShowRanking.setOnClickListener {
            isRankingActive = true
            fetchRanking()
            updateUi()
        }

        buttonBackFromRanking.setOnClickListener {
            isRankingActive = false
            updateUi()
        }

        updateUi()

        // Manejo del botón atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isGameActive) {
                    viewModel.exitGame()
                    updateUi()
                } else if (isRankingActive) {
                    isRankingActive = false
                    updateUi()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
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
        Toast.makeText(requireContext(), if (isCorrect) "¡Correcto!" else "Incorrecto", Toast.LENGTH_SHORT).show()
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadNewQuestion()
            updateUi()
        }
    }

    // para guardar datos en Firebase
    private fun saveScoreToFirebase() {
        val email = auth.currentUser?.email ?: return
        if (viewModel.score == 0) return

        val scoreData = hashMapOf(
            "puntuacion" to viewModel.score,
            "fecha" to com.google.firebase.Timestamp.now()
        )

        // Guarda en: usuarios -> [email] -> partidas -> [id_aleatorio]
        db.collection("users").document(email)
            .collection("scores").add(scoreData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Puntuación guardada en la nube", Toast.LENGTH_SHORT).show()
            }
    }
/*
    private fun updateUi() {
        val currentUser = auth.currentUser

        // ESTADO 1: No logueado
        if (currentUser == null) {
            authContainer.visibility = View.VISIBLE
            gameSelectionContainer.visibility = View.GONE
            triviaContainer.visibility = View.GONE
            return
        }

        // Si llegamos aquí, hay usuario. Ocultamos Auth.
        authContainer.visibility = View.GONE

        // ESTADO 2: Jugando
        if (viewModel.isGameActive) {
            gameSelectionContainer.visibility = View.GONE
            triviaContainer.visibility = View.VISIBLE

            val textScore = view?.findViewById<TextView>(R.id.text_score)
            val textQuestion = view?.findViewById<TextView>(R.id.text_question)
            val btnA = view?.findViewById<Button>(R.id.button_option_a)
            val btnB = view?.findViewById<Button>(R.id.button_option_b)
            val btnC = view?.findViewById<Button>(R.id.button_option_c)

            textScore?.text = "Score: ${viewModel.score}"

            viewModel.currentQuestion?.let { q ->
                textQuestion?.text = q.questionText
                btnA?.text = q.optionA
                btnB?.text = q.optionB
                btnC?.text = q.optionC
                btnA?.visibility = View.VISIBLE
                btnB?.visibility = View.VISIBLE
                btnC?.visibility = View.VISIBLE
            } ?: run {
                textQuestion?.text = "Game Over! Score: ${viewModel.score}"
                btnA?.visibility = View.GONE
                btnB?.visibility = View.GONE
                btnC?.visibility = View.GONE
            }
        }
        // ESTADO 3: Menú de Selección
        else {
            gameSelectionContainer.visibility = View.VISIBLE
            triviaContainer.visibility = View.GONE

            val textLastScore = view?.findViewById<TextView>(R.id.text_last_score)
            if (viewModel.score > 0) {
                textLastScore?.text = "Last Score: ${viewModel.score}"
                textLastScore?.visibility = View.VISIBLE
            } else {
                textLastScore?.visibility = View.GONE
            }
        }
    }
*/
    private fun updateUi() {
        val currentUser = auth.currentUser

        // ESTADO 1: Usuario no logueado (Prioridad máxima)
        if (currentUser == null) {
            authContainer.visibility = View.VISIBLE
            gameSelectionContainer.visibility = View.GONE
            triviaContainer.visibility = View.GONE
            rankingContainer.visibility = View.GONE
            return
        }

        // Si hay usuario, el contenedor de Auth siempre estará oculto
        authContainer.visibility = View.GONE

        // ESTADO 2: Visualización del Ranking
        if (isRankingActive) {
            rankingContainer.visibility = View.VISIBLE
            gameSelectionContainer.visibility = View.GONE
            triviaContainer.visibility = View.GONE
            return
        }

        // ESTADO 3: Juego en curso
        if (viewModel.isGameActive) {
            triviaContainer.visibility = View.VISIBLE
            gameSelectionContainer.visibility = View.GONE
            rankingContainer.visibility = View.GONE

            val textScore = view?.findViewById<TextView>(R.id.text_score)
            val textQuestion = view?.findViewById<TextView>(R.id.text_question)
            val btnA = view?.findViewById<Button>(R.id.button_option_a)
            val btnB = view?.findViewById<Button>(R.id.button_option_b)
            val btnC = view?.findViewById<Button>(R.id.button_option_c)

            textScore?.text = "Score: ${viewModel.score}"

            viewModel.currentQuestion?.let { q ->
                textQuestion?.text = q.questionText
                btnA?.text = q.optionA
                btnB?.text = q.optionB
                btnC?.text = q.optionC
                btnA?.visibility = View.VISIBLE
                btnB?.visibility = View.VISIBLE
                btnC?.visibility = View.VISIBLE
            } ?: run {
                // Caso de fin de juego (no hay más preguntas)
                textQuestion?.text = "Game Over! Score: ${viewModel.score}"
                btnA?.visibility = View.GONE
                btnB?.visibility = View.GONE
                btnC?.visibility = View.GONE
            }
        }
        // ESTADO 4: Menú de Selección (Estado por defecto)
        else {
            gameSelectionContainer.visibility = View.VISIBLE
            triviaContainer.visibility = View.GONE
            rankingContainer.visibility = View.GONE

            val textLastScore = view?.findViewById<TextView>(R.id.text_last_score)
            if (viewModel.score > 0) {
                textLastScore?.text = "Last Score: ${viewModel.score}"
                textLastScore?.visibility = View.VISIBLE
            } else {
                textLastScore?.visibility = View.GONE
            }
        }
    }

    private fun fetchRanking() {
        val rankingText = view?.findViewById<TextView>(R.id.text_ranking_list)
        rankingText?.text = "Calculando puntuaciones..."

        db.collection("users").get().addOnSuccessListener { userDocs ->
            val results = mutableListOf<Pair<String, Int>>()
            var usersProcessed = 0

            if (userDocs.isEmpty) {
                rankingText?.text = "No hay usuarios registrados."
                return@addOnSuccessListener
            }

            for (userDoc in userDocs) {
                val email = userDoc.id
                // consulta a la subcolección de puntuaciones de cada usuario
                db.collection("users").document(email).collection("scores").get()
                    .addOnSuccessListener { scoreDocs ->
                        var totalScore = 0
                        for (scoreDoc in scoreDocs) {
                            totalScore += (scoreDoc.getLong("puntuacion") ?: 0).toInt()
                        }
                        results.add(Pair(email, totalScore))
                        usersProcessed++

                        // mostramos el ranking
                        if (usersProcessed == userDocs.size()) {
                            displayRanking(results)
                        }
                    }
            }
        }
    }

    private fun displayRanking(results: List<Pair<String, Int>>) {
        val rankingText = view?.findViewById<TextView>(R.id.text_ranking_list)

        // de mayor a menor puntuación
        val sortedRanking = results.sortedByDescending { it.second }

        val sb = StringBuilder()
        sortedRanking.forEachIndexed { index, pair ->
            val medalla = when(index) {
                0 -> "🥇 "
                1 -> "🥈 "
                2 -> "🥉 "
                else -> "${index + 1}. "
            }
            sb.append("$medalla${pair.first}\nTotal: ${pair.second} pts\n\n")
        }
        rankingText?.text = sb.toString()
    }
}
package com.example.androiddevelopers.ui.game

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.androiddevelopers.R

class GameFragment :  Fragment(R.layout.fragment_game) {

    private val viewModel: GameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.text_game)

        textView.text = viewModel.text
    }
}
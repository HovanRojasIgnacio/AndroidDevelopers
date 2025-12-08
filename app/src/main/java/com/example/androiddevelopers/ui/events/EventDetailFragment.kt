package com.example.androiddevelopers.ui.events

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.androiddevelopers.R
import com.example.androiddevelopers.domain.HistoricalEvent
import com.example.androiddevelopers.presentation.EventsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventDetailFragment: Fragment(R.layout.fragment_event_detail) {

    private val viewModel: EventsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId") ?: -1

        if (eventId != -1) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.events.collectLatest { events ->
                    val event = events.find { it.id == eventId }
                    event?.let {
                        setupView(view, it)
                    } ?: run {
                        setupErrorView(view)
                    }
                }
            }
        } else {
            setupErrorView(view)
        }
    }

    private fun setupView(view: View, event: HistoricalEvent) {
        view.findViewById<TextView>(R.id.detail_title).text = event.title
        view.findViewById<TextView>(R.id.detail_date).text = event.year
        view.findViewById<TextView>(R.id.detail_description).text = event.detailedDescription
        val imageView = view.findViewById<ImageView>(R.id.detail_image)
        val imageUrl = event.imageUrl?.trim()

        if (!imageUrl.isNullOrEmpty()) {
            imageView.isVisible = true
            imageView.load(imageUrl) {
                crossfade(true)

                // Let's use a standard Android icon we know is dark
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image) // This is a gray "broken image" icon

                // THIS IS THE MOST IMPORTANT PART:
                listener(
                    onError = { request, result ->
                        Log.e("CoilError", "Image load failed!")
                        Log.e("CoilError", "URL: ${request.data}")
                        Log.e("CoilError", "Error: ", result.throwable) // This will print the full error
                    },
                    onSuccess = { request, result ->
                        Log.d("CoilSuccess", "Image loaded successfully: ${request.data}")
                    }
                )
            }
        } else {
            imageView.isVisible = false
            Log.e("CoilError", "URL was null or empty for ${event.title}")
        }
    }

    private fun setupErrorView(view: View) {
        view.findViewById<TextView>(R.id.detail_title).text = "Evento no encontrado"
        view.findViewById<TextView>(R.id.detail_description).text = "No se pudo cargar la información del evento."
    }
}
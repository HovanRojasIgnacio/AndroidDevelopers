package com.example.androiddevelopers.ui.events

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.androiddevelopers.R
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs


class EventDetailFragment: Fragment(R.layout.fragment_event_detail) {

    private val viewModel: EventsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val eventId = arguments?.getInt("eventId") ?: -1

        if (eventId != -1) {
            val event = viewModel.getEventById(eventId)
            event?.let {
                setupView(view, it)
            }
        } else {
            setupErrorView(view)
        }
    }

    private fun setupView(view: View, event: HistoricEvent) {
        view.findViewById<TextView>(R.id.detail_title).text = event.title
        view.findViewById<TextView>(R.id.detail_date).text = event.date
        view.findViewById<TextView>(R.id.detail_description).text = event.detailedDescription
    }

    private fun setupErrorView(view: View) {
        view.findViewById<TextView>(R.id.detail_title).text = "Evento no encontrado"
        view.findViewById<TextView>(R.id.detail_description).text = "No se pudo cargar la información del evento."
    }
}
package com.example.androiddevelopers.ui.events

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddevelopers.R

class EventsFragment : Fragment(R.layout.fragment_events) {

    private val viewModel: EventsViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupAdapter()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.events_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupAdapter() {
        val adapter = HistoricEventAdapter()
        adapter.events = viewModel.events
        adapter.onItemClick = { event ->
            navigateToEventDetail(event.id)
        }
        recyclerView.adapter = adapter
    }

    private fun navigateToEventDetail(eventId: Int) {

        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.eventDetailFragment, bundle)
    }
}
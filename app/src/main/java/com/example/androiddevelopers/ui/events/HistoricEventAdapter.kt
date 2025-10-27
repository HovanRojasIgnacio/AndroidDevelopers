package com.example.androiddevelopers.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddevelopers.R


data class HistoricEvent(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val detailedDescription: String,
    )


class HistoricEventAdapter : RecyclerView.Adapter<HistoricEventAdapter.EventViewHolder>() {

    var events: List<HistoricEvent> = emptyList()
    var onItemClick: ((HistoricEvent) -> Unit)? = null

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.event_title)
        val date: TextView = itemView.findViewById(R.id.event_date)
        val shortDescription: TextView = itemView.findViewById(R.id.event_short_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.date.text = event.date
        holder.shortDescription.text = event.shortDescription

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(event)
        }
    }

    override fun getItemCount() = events.size
}
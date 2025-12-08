package com.example.androiddevelopers.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.androiddevelopers.R
import com.example.androiddevelopers.domain.HistoricalEvent

class HistoricEventAdapter :
    RecyclerView.Adapter<HistoricEventAdapter.EventViewHolder>() {

    var events: List<HistoricalEvent> = emptyList()
    var onItemClick: ((HistoricalEvent) -> Unit)? = null

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.event_title)
        val date: TextView = itemView.findViewById(R.id.event_date)
        val shortDescription: TextView =
            itemView.findViewById(R.id.event_short_description)
        val image: ImageView =
            itemView.findViewById(R.id.event_image)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.date.text = event.year
        holder.shortDescription.text = event.description
        val imageUrl = event.imageUrl

        if (!imageUrl.isNullOrEmpty()) {
            holder.image.load(imageUrl) {
                crossfade(true)
                error(android.R.drawable.ic_menu_report_image)
            }
        } else {
            Log.e("CoilError", "URL was null or empty for ${event.title}")
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(event)
        }

    }

    override fun getItemCount() = events.size
}
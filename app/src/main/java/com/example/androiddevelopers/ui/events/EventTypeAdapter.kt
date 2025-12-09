package com.example.androiddevelopers.ui.events // O donde manejes tus Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddevelopers.R

enum class EventType(val typeName: String, val apiPath: String) {
    EVENTS("Eventos", "events"),
    BIRTHS("Nacimientos", "births"),
    DEATHS("Fallecimientos", "deaths")
}

// 2. La Clase Adaptadora (Adapter) para el RecyclerView
class EventTypeAdapter(private val onTypeClicked: (EventType) -> Unit) :
    ListAdapter<EventType, EventTypeAdapter.TypeViewHolder>(TypeDiffCallback()) {

    // Almacena el tipo de evento actualmente seleccionado para el resaltado visual
    private var selectedType: EventType = EventType.EVENTS

    /**
     * Actualiza el estado de selección de los chips, llamado desde el ViewModel.
     */
    fun setCurrentSelection(type: EventType) {
        if (selectedType != type) {
            // Usa ListAdapter para notificar solo los cambios necesarios
            val oldPosition = currentList.indexOf(selectedType)
            val newPosition = currentList.indexOf(type)

            selectedType = type

            if (oldPosition != -1) notifyItemChanged(oldPosition)
            if (newPosition != -1) notifyItemChanged(newPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TypeViewHolder {
        // Inflado manual de la vista del chip
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_event_type, // Asumimos que este es tu layout de chip
            parent,
            false
        )
        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val type = getItem(position)
        // Pasa el estado de selección al ViewHolder
        holder.bind(type, type == selectedType)
    }

    // 3. El ViewHolder (Vista de un solo chip)
    inner class TypeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Acceso a la vista (TextView) usando findViewById
        private val txtEventType: TextView =
            itemView.findViewById(R.id.txt_event_type)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val type = getItem(adapterPosition)
                    onTypeClicked(type) // Llama a viewModel::setEventType
                }
            }
        }

        fun bind(type: EventType, isSelected: Boolean) {
            txtEventType.text = type.typeName
            // El atributo 'isSelected' activa el selector de color/fondo que debes
            // definir para que el chip se resalte visualmente.
            txtEventType.isSelected = isSelected
        }
    }
}

// 4. Callback para la eficiencia del ListAdapter
class TypeDiffCallback : DiffUtil.ItemCallback<EventType>() {
    override fun areItemsTheSame(
        oldItem: EventType,
        newItem: EventType
    ): Boolean {
        // Los ítems se consideran iguales si tienen el mismo path de API
        return oldItem.apiPath == newItem.apiPath
    }

    override fun areContentsTheSame(
        oldItem: EventType,
        newItem: EventType
    ): Boolean {
        // Como es un enum, si son el mismo ítem, el contenido es el mismo
        return oldItem == newItem
    }
}
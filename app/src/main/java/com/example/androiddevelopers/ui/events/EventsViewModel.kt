package com.example.androiddevelopers.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevelopers.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {
    private val repository = HistoricalEventsRepository()

    private val _events = MutableStateFlow<List<HistoricEvent>>(emptyList())
    val events: StateFlow<List<HistoricEvent>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repository.getTodayHistoricalEvents()
            result.fold(
                onSuccess = { events ->
                    _events.value = events
                },
                onFailure = { error ->
                    _error.value = "Error al cargar eventos: ${error.message}"
                    _events.value = getDefaultEvents()
                }
            )
            _isLoading.value = false
        }
    }

    private fun getDefaultEvents(): List<HistoricEvent> {
        return listOf(
            HistoricEvent(
                id = 1,
                title = "Caída de Babilonia",
                date = "539 a. C.",
                shortDescription = "Ciro el Grande conquista Babilonia",
                detailedDescription = "En el 29 de octubre de 539 a. C., Ciro el Grande tomó Babilonia, poniendo fin al Imperio neobabilónico y permitiendo el retorno de los judíos exiliados."
            ),
            HistoricEvent(
                id = 2,
                title = "Batalla del Puente Milvio",
                date = "312 d. C.",
                shortDescription = "Constantino regresa a Roma tras vencer en el Puente Milvio",
                detailedDescription = "El 29 de octubre de 312, el emperador Constantino el Grande regresó a Roma después de su victoria en la Batalla del Puente Milvio, acontecimiento clave en su ascenso y posterior conversión al cristianismo."
            ),
            HistoricEvent(
                id = 3,
                title = "Estreno de la ópera Don Giovanni",
                date = "1787",
                shortDescription = "Mozart presenta Don Giovanni en Praga",
                detailedDescription = "El 29 de octubre de 1787 se estrenó la ópera *Don Giovanni*, compuesta por Wolfgang Amadeus Mozart, en el Teatro Estatal de Praga."
            )
        )
    }

    fun getEventById(id: Int): HistoricEvent? {
        return _events.value.find { it.id == id }
    }
}
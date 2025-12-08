package com.example.androiddevelopers.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsViewModel : ViewModel() {
    private val repository = HistoricalEventsRepository()

    private val _currentDate = MutableStateFlow(Calendar.getInstance())
    val currentDate: StateFlow<Calendar> = _currentDate.asStateFlow()

    private val _events = MutableStateFlow<List<HistoricEvent>>(emptyList())
    val events: StateFlow<List<HistoricEvent>> = _events.asStateFlow()

    private val _currentEventType = MutableStateFlow(EventType.EVENTS)
    val currentEventType: StateFlow<EventType> = _currentEventType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        setDate(_currentDate.value)
    }

    fun setDate(newCalendar: Calendar) {
        _currentDate.value = newCalendar.clone() as Calendar
        loadEvents()
    }

    fun loadEvents() {
        _isLoading.value = true
        _error.value = null

        val calendar = _currentDate.value
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val eventType = _currentEventType.value.apiPath

        viewModelScope.launch {
            val result = repository.getEventsForDate(eventType, month, day)

            if (result.isSuccess) {
                _events.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value =
                    "Error al cargar eventos: ${result.exceptionOrNull()?.message}"
                _events.value = getDefaultEvents()
            }
            _isLoading.value = false
        }
    }

    fun getFormattedDateComponents(calendar: Calendar): Pair<String, String> {
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()

        val monthFormatter = SimpleDateFormat("MMMM", Locale("es", "ES"))
        val monthName = monthFormatter.format(calendar.time)
            // Convierte la primera letra a mayúscula
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale(
                        "es",
                        "ES"
                    )
                ) else it.toString()
            }

        return Pair(day, monthName) // Retorna el día y el mes
    }

    fun setEventType(eventType: EventType) {
        if (_currentEventType.value != eventType) {
            _currentEventType.value = eventType
            loadEvents()
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

}
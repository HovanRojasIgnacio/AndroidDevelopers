package com.example.androiddevelopers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevelopers.data.repository.HistoricalEventsRepository
import com.example.androiddevelopers.domain.HistoricalEvent
import com.example.androiddevelopers.ui.events.EventType
import com.example.androiddevelopers.ui.events.HistoricalPeriod
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

    /** Contiene los eventos después de aplicar los filtros, es decir solo los que se
     * muestran en la UI */
    private val _events = MutableStateFlow<List<HistoricalEvent>>(emptyList())
    val events: StateFlow<List<HistoricalEvent>> = _events.asStateFlow()

    private val _currentEventType = MutableStateFlow(EventType.EVENTS)
    val currentEventType: StateFlow<EventType> = _currentEventType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)

    /** Guarda la lista completa de eventos cargados de la API para el día y EventType actual,
     * antes de aplicar el filtro de época (HistoricalPeriod). */
    private val _rawEventsFromApi =
        MutableStateFlow<List<HistoricalEvent>>(emptyList())

    // Guarda el conjunto de épocas históricas seleccionadas por el usuario
    private val _activePeriods =
        MutableStateFlow<Set<HistoricalPeriod>>(emptySet())
    val activePeriods: StateFlow<Set<HistoricalPeriod>> =
        _activePeriods.asStateFlow()

    // Para ordenar desde el más reciente o desde el más antiguo.
    public enum class SortOrder { OLDEST, NEWEST }
    private val _currentSortOrder = MutableStateFlow(SortOrder.OLDEST)
    val currentSortOrder = _currentSortOrder.asStateFlow()

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
                val newEvents = result.getOrNull() ?: emptyList()
                _rawEventsFromApi.value = newEvents
                filterEventsByPeriod(newEvents, _activePeriods.value)
            } else {
                _error.value =
                    "Error al cargar eventos: ${result.exceptionOrNull()?.message}"
                _events.value = getDefaultEvents()
            }
            _isLoading.value = false
        }
    }

    /* Actualiza los filtros de época */
    fun updatePeriods(selectedPeriods: Set<HistoricalPeriod>) {
        _activePeriods.value = selectedPeriods
        // Filtra los datos brutos con los nuevos periodos
        filterEventsByPeriod(_rawEventsFromApi.value, selectedPeriods)
    }

    /* Aplica el filtro de época a una lista de eventos */
    /* Aplica el filtro de época a una lista de eventos y luego los ordena */
    private fun filterEventsByPeriod(
        rawEvents: List<HistoricalEvent>,
        periods: Set<HistoricalPeriod>
    ) {
        val filteredList = if (periods.isEmpty()) {
            rawEvents
        } else {
            rawEvents.filter { event ->
                val rawYearString =
                    event.year.split(" ").firstOrNull() ?: return@filter false
                val isBC = event.year.contains("a. C.", ignoreCase = true)
                val numericYear =
                    rawYearString.filter { it.isDigit() }.toIntOrNull()
                        ?: return@filter false
                val eventYear = if (isBC) -numericYear else numericYear

                periods.any { period ->
                    eventYear >= period.startYear && eventYear <= period.endYear
                }
            }
        }

        val sortedEvents = if (_currentSortOrder.value == SortOrder.OLDEST) {
            filteredList.sortedBy { parseYear(it.year) ?: 0 }
        } else {
            filteredList.sortedByDescending { parseYear(it.year) ?: 0 }
        }

        _events.value = sortedEvents
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

    /**
     * Cambia el orden de los eventos o de más antiguo a más reciente o al revés
     */
    fun updateSortOrder(order: SortOrder) {
        _currentSortOrder.value = order
        filterEventsByPeriod(_rawEventsFromApi.value, _activePeriods.value)
    }

    private fun parseYear(yearString: String): Int? {
        val rawYearString = yearString.split(" ").firstOrNull() ?: return null
        val isBC = yearString.contains("a. C.", ignoreCase = true)
        val numericYear = rawYearString.filter { it.isDigit() }.toIntOrNull() ?: return null
        return if (isBC) -numericYear else numericYear
    }

    private fun getDefaultEvents(): List<HistoricalEvent> {
        return listOf(
            HistoricalEvent(
                id = 1,
                title = "Caída de Babilonia",
                year = "539 a. C.",
                description = "Ciro el Grande conquista Babilonia",
                detailedDescription = "En el 29 de octubre de 539 a. C., Ciro el Grande tomó Babilonia, poniendo fin al Imperio neobabilónico y permitiendo el retorno de los judíos exiliados.",
                imageUrl = null,
                articleUrl = null
            ),
            HistoricalEvent(
                id = 2,
                title = "Batalla del Puente Milvio",
                year = "312 d. C.",
                description = "Constantino regresa a Roma tras vencer en el Puente Milvio",
                detailedDescription = "El 29 de octubre de 312, el emperador Constantino el Grande regresó a Roma después de su victoria en la Batalla del Puente Milvio.",
                imageUrl = null,
                articleUrl = null
            ),
            HistoricalEvent(
                id = 3,
                title = "Estreno de Don Giovanni",
                year = "1787",
                description = "Mozart presenta Don Giovanni en Praga",
                detailedDescription = "El 29 de octubre de 1787 se estrenó la ópera Don Giovanni, compuesta por Wolfgang Amadeus Mozart, en el Teatro Estatal de Praga.",
                imageUrl = null,
                articleUrl = null
            )
        )
    }

    fun getEventById(id: Int): HistoricalEvent? {
        return _events.value.find { it.id == id }
    }


}
package com.example.androiddevelopers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevelopers.data.repository.HistoricalEventsRepository
import com.example.androiddevelopers.domain.HistoricalEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsViewModel : ViewModel() {
    private val repository = HistoricalEventsRepository()

    // --- ESTADO DE FECHA ---
    private val _currentDate = MutableStateFlow(Calendar.getInstance())
    val currentDate: StateFlow<Calendar> = _currentDate.asStateFlow()
    // ----------------------

    private val _events = MutableStateFlow<List<HistoricalEvent>>(emptyList())
    val events: StateFlow<List<HistoricalEvent>> = _events.asStateFlow()

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

        // Obtener mes y día de la fecha almacenada en el StateFlow
        val calendar = _currentDate.value
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        viewModelScope.launch {
            // val result = repository.getTodayHistoricalEvents()
            val result = repository.getEventsForDate(month, day)

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

    fun goToPreviousDay() {
        // Clonar la fecha actual, restar un día y actualizar el StateFlow
        val newDate = _currentDate.value.clone() as Calendar
        newDate.add(Calendar.DAY_OF_YEAR, -1)
        _currentDate.value = newDate

        loadEvents() // Recargar los eventos para la nueva fecha
    }

    fun goToNextDay() {
        // Clonar la fecha actual, sumar un día y actualizar el StateFlow
        val newDate = _currentDate.value.clone() as Calendar
        newDate.add(Calendar.DAY_OF_YEAR, 1)
        _currentDate.value = newDate

        loadEvents() // Recargar los eventos para la nueva fecha
    }

    // EventsViewModel.kt

    // Renombrar la función para ser más específica
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
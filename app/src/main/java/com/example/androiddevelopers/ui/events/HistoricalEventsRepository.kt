package com.example.androiddevelopers.ui.events

import com.example.androiddevelopers.data.remote.Apis
import com.example.androiddevelopers.data.remote.WikipediaApi
import com.example.androiddevelopers.data.remote.WikipediaEvent
import com.example.androiddevelopers.data.repository.toDomain
import com.example.androiddevelopers.domain.HistoricalEvent
import java.io.IOException
import java.util.Calendar

class HistoricalEventsRepository(
    // Inyección de dependencias (Práctica 10): Pasamos la API por constructor
    private val api: WikipediaApi = Apis.wikipedia
) {

    private fun buildDetailedDescription(event: WikipediaEvent): String {
        return "En el año ${event.year}: ${event.text}"
    }

    /**
     * Obtiene los eventos de HOY calculando la fecha automáticamente.
     * Reutiliza getEventsForDate para no duplicar código.
     */
    suspend fun getTodayHistoricalEvents(): Result<List<HistoricalEvent>> {
        val today = Calendar.getInstance()
        // Calendar.MONTH empieza en 0, por eso sumamos 1
        return getEventsForDate(
            EventType.EVENTS,
            today.get(Calendar.MONTH) + 1, today.get(
                Calendar.DAY_OF_MONTH
            )
        )
    }

    /**
     * Obtiene eventos para una fecha específica.
     * Esta es la función que te faltaba.
     */
    suspend fun getEventsForDate(
        eventType: EventType,
        month: Int,
        day: Int
    ): Result<List<HistoricalEvent>> {
        return try {
            // 1. Llamada a la API (Retrofit - Práctica 8)
            val response = api.getEventsOnThisDay(eventType.apiPath, month, day)

            if (response.isSuccessful) {
                val body = response.body()

                val dtos = when (eventType) {
                    EventType.EVENTS -> body?.events ?: emptyList()
                    EventType.BIRTHS -> body?.births ?: emptyList()
                    EventType.DEATHS -> body?.deaths ?: emptyList()
                }

                val events =
                    dtos.mapIndexed { index, dto ->
                        dto.toDomain(index = index + 1)
                    }

                if (events.isNotEmpty()) {
                    Result.success(events)
                } else {
                    Result.success(getDefaultEvents())
                }
            } else {
                Result.success(getDefaultEvents())
            }
        } catch (e: IOException) {
            // Error de red
            Result.failure(e)
        } catch (e: Exception) {
            // Error genérico
            Result.failure(e)
        }
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
}
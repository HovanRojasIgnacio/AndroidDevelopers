package com.example.androiddevelopers.ui.events

import java.util.Calendar
import com.example.androiddevelopers.ui.events.Apis
import com.example.androiddevelopers.ui.events.HistoricEvent
import java.net.UnknownHostException

class HistoricalEventsRepository {

    suspend fun getTodayHistoricalEvents(): Result<List<HistoricEvent>> {
        return try {
            val today = Calendar.getInstance()
            val month = today.get(Calendar.MONTH) + 1
            val day = today.get(Calendar.DAY_OF_MONTH)

            val response = Apis.wikipedia.getEventsOnThisDay(month, day)

            if (response.isSuccessful) {
                val wikipediaEvents = response.body()?.events ?: emptyList()

                val events = wikipediaEvents.mapIndexed { index, wikipediaEvent ->
                    HistoricEvent(
                        id = index + 1,
                        title = wikipediaEvent.pages.firstOrNull()?.title?.replace("_", " ") ?: "Evento Histórico",
                        date = wikipediaEvent.year,
                        shortDescription = wikipediaEvent.text,
                        detailedDescription = buildDetailedDescription(wikipediaEvent),
                        imageUrl = wikipediaEvent.pages.firstOrNull()?.thumbnail?.source
                    )
                }
                Result.success(events)
            } else {
                //si falla se usan los hardcodeados
                Result.success(getDefaultEvents())
            }
        } catch (e: UnknownHostException) {
            println("ERROR DE CONEXIÓN: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun buildDetailedDescription(event: WikipediaEvent): String {
        return "En el año ${event.year}: ${event.text}"
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
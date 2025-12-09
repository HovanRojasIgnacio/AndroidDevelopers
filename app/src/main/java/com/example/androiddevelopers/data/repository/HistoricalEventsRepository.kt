package com.example.androiddevelopers.data.repository

import com.example.androiddevelopers.data.remote.Apis
import com.example.androiddevelopers.data.remote.WikipediaApi
import com.example.androiddevelopers.domain.HistoricalEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.IOException


class HistoricalEventsRepository(
    // Inyección de dependencias (Práctica 10): Pasamos la API por constructor
    private val api: WikipediaApi = Apis.wikipedia
) {
    
    /**
     * Obtiene eventos para una fecha específica.
     * Esta es la función que te faltaba.
     * ESTA FUNCIÓN TARDA MENOS TIEMPO PERO EN DEATHS NO CARGA ALGUNAS FOTOS
     */
    /* suspend fun getEventsForDate(
         type: String,
         month: Int,
         day: Int
     ): Result<List<HistoricalEvent>> {
         return try {
             // 1. Llamada a la API (Retrofit - Práctica 8)
             val response = api.getEventsOnThisDay(type, month, day)

             if (response.isSuccessful) {

                 var dtos = emptyList<WikipediaEvent>()
                 if (type.equals("events")) {
                     dtos = response.body()?.events ?: emptyList()
                 } else if (type.equals("births")) {
                     dtos = response.body()?.births ?: emptyList()
                 } else if (type.equals("deaths")) {
                     dtos = response.body()?.deaths ?: emptyList()
                 }

                 val events = dtos.mapIndexed { index, dto ->
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
     }*/

    // Función de reintentos con backoff
    suspend fun <T> retry(times: Int = 3, block: suspend () -> T): T {
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                delay(200L * (attempt + 1)) // backoff progresivo
            }
        }
        return block()
    }

    suspend fun getEventsForDate(
        type: String,
        month: Int,
        day: Int
    ): Result<List<HistoricalEvent>> = coroutineScope {
        try {
            val response = api.getEventsOnThisDay(type, month, day)

            if (!response.isSuccessful) return@coroutineScope Result.success(
                getDefaultEvents()
            )
            val body = response.body() ?: return@coroutineScope Result.success(
                getDefaultEvents()
            )

            val dtos = when (type) {
                "events" -> body.events
                "births" -> body.births
                "deaths" -> body.deaths
                else -> null
            } ?: emptyList()

            val initialEvents = dtos.toDomainList()
            val needsImageSearch = type == "births" || type == "deaths"

            // Limitar concurrencia a 5 llamadas simultáneas
            val semaphore = Semaphore(permits = 5)

            val finalEvents = withContext(Dispatchers.IO) {
                initialEvents.map { event ->
                    async {
                        if (!needsImageSearch || !event.imageUrl.isNullOrEmpty() || event.title.isNullOrEmpty()) {
                            return@async event
                        }

                        semaphore.withPermit {
                            runCatching {
                                val cleanTitle = event.title
                                    .replace("^\\d{4}:\\s*".toRegex(), "")
                                    .replace(" ", "_")

                                retry {
                                    api.getPageSummary(cleanTitle).body()
                                        ?.let { summary ->
                                            val newImageUrl =
                                                summary.originalimage?.source
                                                    ?: summary.thumbnail?.source
                                            event.copy(imageUrl = newImageUrl)
                                        } ?: event
                                }
                            }.getOrElse { event }
                        }
                    }
                }.awaitAll()
            }

            Result.success(finalEvents.ifEmpty { getDefaultEvents() })
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
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
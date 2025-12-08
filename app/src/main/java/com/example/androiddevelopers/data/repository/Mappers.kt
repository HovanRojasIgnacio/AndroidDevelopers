package com.example.androiddevelopers.data.repository

import com.example.androiddevelopers.data.remote.WikipediaEvent
import com.example.androiddevelopers.domain.HistoricalEvent

// Ahora la función recibe el 'id' que le asignaremos
fun WikipediaEvent.toDomain(index: Int): HistoricalEvent {
    val firstPage = this.pages.firstOrNull()
    val fullDescription = firstPage?.extract
        ?: "En el año ${this.year}: ${this.text}"

    return HistoricalEvent(
        id = index,
        year = this.year,
        description = this.text,
        detailedDescription = fullDescription,
        title = firstPage?.title?.replace("_", " ") ?: "Evento Histórico",
        imageUrl = firstPage?.originalimage?.source ?: firstPage?.thumbnail?.source,
        articleUrl = firstPage?.contentUrls?.desktop?.page
    )
}

fun List<WikipediaEvent>.toDomainList(): List<HistoricalEvent> {
    return this.mapIndexed { index, dto ->
        dto.toDomain(index + 1)
    }
}
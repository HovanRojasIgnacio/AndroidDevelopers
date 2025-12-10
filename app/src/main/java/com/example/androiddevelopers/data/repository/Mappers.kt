package com.example.androiddevelopers.data.repository

import com.example.androiddevelopers.data.remote.WikipediaEvent
import com.example.androiddevelopers.domain.HistoricalEvent

/**
 * Ahora la función recibe el 'id' que le asignaremos
 * Añadí este código porque en fallicimentos a veces se cargaba un título Anexo 2022....
 * y tampoco se cargaba la imagen.
 */
fun WikipediaEvent.toDomain(index: Int): HistoricalEvent {
    val firstPage = this.pages.firstOrNull()
    val pageTitleRaw = firstPage?.title?.replace("_", " ")
    val isGenericTitle =
        pageTitleRaw?.contains("Anexo", ignoreCase = true) == true ||
                pageTitleRaw?.contains("Lista", ignoreCase = true) == true
    val pageTitle =
        pageTitleRaw.takeIf { !isGenericTitle } // Usar solo si no es genérico
    val rawText = this.text
    val namePart = rawText.split(",").firstOrNull() ?: rawText
    val removedParentheses =
        namePart.replace("\\s*\\([^)]*\\)".toRegex(), "").trim()
    val cleanedName =
        removedParentheses.replace("^\\d{4}:\\s*".toRegex(), "").trim()
    val finalTitle = pageTitle ?: cleanedName.takeIf { it.isNotEmpty() }
    ?: "Evento Histórico"
    val fullDescription = firstPage?.extract
        ?: "En el año ${this.year}: ${this.text}"

    return HistoricalEvent(
        id = index,
        year = this.year,
        description = this.text,
        detailedDescription = fullDescription,
        title = finalTitle,
        imageUrl = firstPage?.originalimage?.source
            ?: firstPage?.thumbnail?.source,
        articleUrl = firstPage?.contentUrls?.desktop?.page
    )
}

fun List<WikipediaEvent>.toDomainList(): List<HistoricalEvent> {
    return this.mapIndexed { index, dto ->
        dto.toDomain(index + 1)
    }
}
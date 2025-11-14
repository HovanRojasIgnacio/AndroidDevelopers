package com.example.androiddevelopers.ui.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikipediaOnThisDayResponse(
    val events: List<WikipediaEvent>
)

@Serializable
data class WikipediaEvent(
    val text: String,
    val year: String,
    val pages: List<WikipediaPage>
)

@Serializable
data class WikipediaPage(
    val title: String,
    @SerialName("content_urls") val contentUrls: WikipediaUrls? = null,
    val thumbnail: WikipediaThumbnail? = null,
    val extract: String? = null
)

@Serializable
data class WikipediaUrls(
    val desktop: WikipediaUrl? = null
)

@Serializable
data class WikipediaUrl(
    val page: String? = null
)

@Serializable
data class WikipediaThumbnail(
    val source: String? = null
)
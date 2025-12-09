package com.example.androiddevelopers.data.remote

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikipediaOnThisDayResponse(
    @SerializedName("events")
    val events: List<WikipediaEvent>?,

    @SerializedName("births")
    val births: List<WikipediaEvent>?,

    @SerializedName("deaths")
    val deaths: List<WikipediaEvent>?
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
    val originalimage: WikipediaThumbnail? = null,
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
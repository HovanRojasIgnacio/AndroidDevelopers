package com.example.androiddevelopers.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApi {

    @GET("api/rest_v1/feed/onthisday/{type}/{month}/{day}")
    suspend fun getEventsOnThisDay(
        @Path("type") type: String, // 'events', 'births', 'deaths', o 'holidays'
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Response<WikipediaOnThisDayResponse>

    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getPageSummary(@Path("title") title: String): Response<WikipediaPage>

}
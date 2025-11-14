package com.example.androiddevelopers.ui.events

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Apis {

    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val userAgentInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }.also { interceptor ->
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", "HistoricalEventsApp/1.0 (https://github.com/example; androidDevelopers@example.com)")
                .header("Accept", "application/json")
                .build()
            chain.proceed(requestWithUserAgent)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://es.wikipedia.org/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikipedia: WikipediaApi by lazy {
        retrofit.create(WikipediaApi::class.java)
    }
}
package com.example.androiddevelopers.data.remote

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

    /*val client = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header(
                    "User-Agent",
                    "HistoricalEventsApp/1.0 (https://github.com/example; androidDevelopers@example.com)"
                )
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
                )
                .build()
            chain.proceed(requestWithUserAgent)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()*/

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)

        // ⭐️ REINTRODUCCIÓN DE LA LÓGICA DE REFERER (FIX 403)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
            builder.header(
                "User-Agent",
                "Mozilla/5.0(Linux;Android 10, K) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/114.0.0.0 Mobile Safari/537.36"
            )
            builder.header("Accept", "application/json")

            // 2. Referer Condicional
            val urlHost = originalRequest.url.host

            // Aplica Referer SOLAMENTE a URLs de descarga de imagen (upload.wikimedia.org)
            if (urlHost.contains("upload.wikimedia.org")) {
                builder.header("Referer", "https://es.wikipedia.org/")
            }

            val requestWithHeaders = builder.build()
            chain.proceed(requestWithHeaders)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://es.wikipedia.org/")
        .client(client) // This correctly uses your client
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikipedia: WikipediaApi by lazy {
        retrofit.create(WikipediaApi::class.java)
    }
}
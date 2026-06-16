package com.jcu.cp3406.cp3406assignment1aqi.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit client for network requests
 * Ensures only one Retrofit instance exists in the app
 */
object RetrofitClient {
    private const val BASE_URL = "https://air-quality-api.open-meteo.com/"

    // Log interceptor: print request/response details for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configure OkHttp client with timeout and interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // Lazy initialize Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Expose API service instance
    val aqiApi: AqiApi by lazy {
        retrofit.create(AqiApi::class.java)
    }
}